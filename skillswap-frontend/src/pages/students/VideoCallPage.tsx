import {
  useCallback,
  useEffect,
  useRef,
  useState,
} from "react";
import {
  useNavigate,
  useParams,
  useSearchParams,
} from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import studentService from "../../services/student/studentService";
import callWebSocketService, {
  type CallSignal,
} from "../../services/video/callWebSocketService";
import {
  getStudentName,
  type StudentProfile,
} from "../../types/student/student.types";

type CallMode = "voice" | "video";

type CallState =
  | "connecting"
  | "calling"
  | "ringing"
  | "connected"
  | "rejected"
  | "ended"
  | "error";

const VideoCallPage = () => {
  const { userId } = useParams<{
    userId: string;
  }>();

  const [searchParams] =
    useSearchParams();

  const navigate = useNavigate();

  const { user } = useAuth();

  const numericUserId =
    Number(userId);

  const conversationId = Number(
    searchParams.get("conversationId")
  );

  const requestedMode =
    searchParams.get("mode") === "voice"
      ? "voice"
      : "video";

      const isInitiator =
  searchParams.get("initiator") === "true";

  const [student, setStudent] =
    useState<StudentProfile | null>(null);

  const [callState, setCallState] =
    useState<CallState>("connecting");

  const [callMode, setCallMode] =
    useState<CallMode>(requestedMode);

  const [error, setError] =
    useState("");

  const [isMuted, setIsMuted] =
    useState(false);

  const [isCameraEnabled, setIsCameraEnabled] =
    useState(requestedMode === "video");

  const [isScreenSharing, setIsScreenSharing] =
    useState(false);

  const [incomingCall, setIncomingCall] =
    useState(false);

  const [remoteStreamReady, setRemoteStreamReady] =
    useState(false);

  const localVideoRef =
    useRef<HTMLVideoElement | null>(null);

  const remoteVideoRef =
    useRef<HTMLVideoElement | null>(null);

  const peerConnectionRef =
    useRef<RTCPeerConnection | null>(null);

  const localStreamRef =
    useRef<MediaStream | null>(null);

  const screenStreamRef =
    useRef<MediaStream | null>(null);

  const remoteUserIdRef =
    useRef<number>(numericUserId);

  const callStartedRef =
    useRef(false);

  const cleanedUpRef =
    useRef(false);

  const configuration: RTCConfiguration = {
    iceServers: [
      {
        urls: "stun:stun.l.google.com:19302",
      },
      {
        urls: "stun:stun1.l.google.com:19302",
      },
    ],
  };

  const targetName = student
    ? getStudentName(student)
    : "Student";

  const sendSignal = useCallback(
    async (
      type:
        | "CALL_REQUEST"
        | "CALL_ACCEPT"
        | "CALL_REJECT"
        | "OFFER"
        | "ANSWER"
        | "ICE_CANDIDATE"
        | "CALL_END",
      payload?: string | null
    ) => {
      await callWebSocketService.sendSignal({
        conversationId,
        targetUserId:
          remoteUserIdRef.current,
        type,
        payload,
      });
    },
    [conversationId]
  );

  const stopLocalStream = useCallback(() => {
    if (localStreamRef.current) {
      localStreamRef.current
        .getTracks()
        .forEach((track) => {
          track.stop();
        });

      localStreamRef.current = null;
    }

    if (screenStreamRef.current) {
      screenStreamRef.current
        .getTracks()
        .forEach((track) => {
          track.stop();
        });

      screenStreamRef.current = null;
    }

    if (localVideoRef.current) {
      localVideoRef.current.srcObject =
        null;
    }
  }, []);

  const closePeerConnection = useCallback(() => {
    if (peerConnectionRef.current) {
      peerConnectionRef.current
        .getSenders()
        .forEach((sender) => {
          try {
            peerConnectionRef.current?.removeTrack(
              sender
            );
          } catch {
            // Ignore cleanup errors.
          }
        });

      peerConnectionRef.current.close();

      peerConnectionRef.current = null;
    }

    if (remoteVideoRef.current) {
      remoteVideoRef.current.srcObject =
        null;
    }

    setRemoteStreamReady(false);
  }, []);

  const cleanupCall = useCallback(() => {
    if (cleanedUpRef.current) {
      return;
    }

    cleanedUpRef.current = true;

    closePeerConnection();
    stopLocalStream();
  }, [
    closePeerConnection,
    stopLocalStream,
  ]);

  const getMedia = useCallback(
    async (mode: CallMode) => {
      const stream =
        await navigator.mediaDevices.getUserMedia(
          {
            audio: true,
            video:
              mode === "video",
          }
        );

      localStreamRef.current =
        stream;

      if (localVideoRef.current) {
        localVideoRef.current.srcObject =
          stream;
      }

      return stream;
    },
    []
  );

  const createPeerConnection =
    useCallback(
      (stream: MediaStream) => {
        const peerConnection =
          new RTCPeerConnection(
            configuration
          );

        peerConnectionRef.current =
          peerConnection;

        stream
          .getTracks()
          .forEach((track) => {
            peerConnection.addTrack(
              track,
              stream
            );
          });

        peerConnection.ontrack = (
          event
        ) => {
          const [remoteStream] =
            event.streams;

          if (
            remoteVideoRef.current &&
            remoteStream
          ) {
            remoteVideoRef.current.srcObject =
              remoteStream;
          }

          setRemoteStreamReady(true);
        };

        peerConnection.onicecandidate =
          (event) => {
            if (!event.candidate) {
              return;
            }

            void sendSignal(
              "ICE_CANDIDATE",
              JSON.stringify(
                event.candidate
              )
            );
          };

        peerConnection.onconnectionstatechange =
          () => {
            const state =
              peerConnection.connectionState;

            if (
              state === "connected"
            ) {
              setCallState(
                "connected"
              );
            }

            if (
              state === "failed" ||
              state === "disconnected"
            ) {
              setError(
                "The call connection was interrupted."
              );
            }

            if (
              state === "closed"
            ) {
              setCallState("ended");
            }
          };

        return peerConnection;
      },
      [sendSignal]
    );

  const createOffer = useCallback(
    async () => {
      const peerConnection =
        peerConnectionRef.current;

      if (!peerConnection) {
        throw new Error(
          "Call connection is not ready."
        );
      }

      const offer =
        await peerConnection.createOffer();

      await peerConnection.setLocalDescription(
        offer
      );

      await sendSignal(
        "OFFER",
        JSON.stringify(offer)
      );
    },
    [sendSignal]
  );

  const handleCallAccept =
    useCallback(
      async () => {
        try {
          setIncomingCall(false);
          setCallState("connecting");
          setError("");

          const stream =
            localStreamRef.current ??
            (await getMedia(callMode));

          if (
            !peerConnectionRef.current
          ) {
            createPeerConnection(
              stream
            );
          }

          await sendSignal(
            "CALL_ACCEPT",
            JSON.stringify({
              mode: callMode,
            })
          );
        } catch (err) {
          console.error(
            "Failed to accept call:",
            err
          );

          setCallState("error");

          setError(
            "Unable to access your microphone or camera."
          );
        }
      },
      [
        callMode,
        createPeerConnection,
        getMedia,
        sendSignal,
      ]
    );

  const handleCallReject =
    useCallback(async () => {
      try {
        await sendSignal(
          "CALL_REJECT",
          JSON.stringify({
            mode: callMode,
          })
        );
      } catch (err) {
        console.error(
          "Failed to reject call:",
          err
        );
      }

      setIncomingCall(false);
      setCallState("rejected");

      cleanupCall();
    }, [
      callMode,
      cleanupCall,
      sendSignal,
    ]);

  const handleIncomingSignal =
    useCallback(
      async (signal: CallSignal) => {
        if (
          signal.conversationId !==
          conversationId
        ) {
          return;
        }

        if (
          signal.senderUserId !==
          remoteUserIdRef.current
        ) {
          return;
        }

        try {
          if (
            signal.type ===
            "CALL_REQUEST"
          ) {
            let mode: CallMode =
              "video";

            if (signal.payload) {
              try {
                const parsed =
                  JSON.parse(
                    signal.payload
                  ) as {
                    mode?: CallMode;
                  };

                if (
                  parsed.mode ===
                  "voice"
                ) {
                  mode = "voice";
                }
              } catch {
                // Keep video as default.
              }
            }

            setCallMode(mode);
            setIncomingCall(true);
            setCallState("ringing");

            return;
          }

          if (
            signal.type ===
            "CALL_ACCEPT"
          ) {
            if (
              !localStreamRef.current
            ) {
              const stream =
                await getMedia(
                  callMode
                );

              createPeerConnection(
                stream
              );
            }

            await createOffer();

            setCallState(
              "connecting"
            );

            return;
          }

          if (
            signal.type ===
            "CALL_REJECT"
          ) {
            setCallState(
              "rejected"
            );

            cleanupCall();

            return;
          }

          if (
            signal.type ===
            "OFFER"
          ) {
            if (
              !signal.payload
            ) {
              return;
            }

            if (
              !localStreamRef.current
            ) {
              const stream =
                await getMedia(
                  callMode
                );

              createPeerConnection(
                stream
              );
            }

            const peerConnection =
              peerConnectionRef.current;

            if (
              !peerConnection
            ) {
              return;
            }

            const offer =
              JSON.parse(
                signal.payload
              ) as RTCSessionDescriptionInit;

            await peerConnection.setRemoteDescription(
              new RTCSessionDescription(
                offer
              )
            );

            const answer =
              await peerConnection.createAnswer();

            await peerConnection.setLocalDescription(
              answer
            );

            await sendSignal(
              "ANSWER",
              JSON.stringify(answer)
            );

            setCallState(
              "connected"
            );

            return;
          }

          if (
            signal.type ===
            "ANSWER"
          ) {
            if (
              !signal.payload
            ) {
              return;
            }

            const peerConnection =
              peerConnectionRef.current;

            if (
              !peerConnection
            ) {
              return;
            }

            const answer =
              JSON.parse(
                signal.payload
              ) as RTCSessionDescriptionInit;

            await peerConnection.setRemoteDescription(
              new RTCSessionDescription(
                answer
              )
            );

            setCallState(
              "connected"
            );

            return;
          }

          if (
            signal.type ===
            "ICE_CANDIDATE"
          ) {
            if (
              !signal.payload
            ) {
              return;
            }

            const peerConnection =
              peerConnectionRef.current;

            if (
              !peerConnection
            ) {
              return;
            }

            const candidate =
              JSON.parse(
                signal.payload
              ) as RTCIceCandidateInit;

            await peerConnection.addIceCandidate(
              new RTCIceCandidate(
                candidate
              )
            );

            return;
          }

          if (
            signal.type ===
            "CALL_END"
          ) {
            setCallState(
              "ended"
            );

            cleanupCall();
          }
        } catch (err) {
          console.error(
            "Failed to process call signal:",
            err
          );

          setCallState("error");

          setError(
            "A call connection error occurred."
          );
        }
      },
      [
        callMode,
        cleanupCall,
        conversationId,
        createOffer,
        createPeerConnection,
        getMedia,
        sendSignal,
      ]
    );

  const startOutgoingCall =
    useCallback(async () => {
      try {
        setError("");
        setCallState("calling");

        const stream =
          await getMedia(callMode);

        createPeerConnection(
          stream
        );

        await sendSignal(
          "CALL_REQUEST",
          JSON.stringify({
            mode: callMode,
          })
        );

        callStartedRef.current =
          true;
      } catch (err) {
        console.error(
          "Failed to start call:",
          err
        );

        setCallState("error");

        setError(
          "Unable to access your microphone or camera. Please check your browser permissions."
        );

        cleanupCall();
      }
    }, [
      callMode,
      cleanupCall,
      createPeerConnection,
      getMedia,
      sendSignal,
    ]);

  const handleEndCall =
    useCallback(async () => {
      try {
        if (
          conversationId > 0
        ) {
          await sendSignal(
            "CALL_END"
          );
        }
      } catch (err) {
        console.error(
          "Failed to send call end signal:",
          err
        );
      }

      setCallState("ended");

      cleanupCall();
    }, [
      cleanupCall,
      conversationId,
      sendSignal,
    ]);

  const toggleMute = () => {
    const stream =
      localStreamRef.current;

    if (!stream) {
      return;
    }

    const audioTracks =
      stream.getAudioTracks();

    audioTracks.forEach(
      (track) => {
        track.enabled =
          !track.enabled;
      }
    );

    setIsMuted(
      audioTracks.length > 0 &&
        !audioTracks[0].enabled
    );
  };

  const toggleCamera = () => {
    const stream =
      localStreamRef.current;

    if (!stream) {
      return;
    }

    const videoTracks =
      stream.getVideoTracks();

    if (videoTracks.length === 0) {
      return;
    }

    videoTracks.forEach(
      (track) => {
        track.enabled =
          !track.enabled;
      }
    );

    setIsCameraEnabled(
      videoTracks[0].enabled
    );
  };

  const toggleScreenShare =
    async () => {
      if (
        !navigator.mediaDevices
          .getDisplayMedia
      ) {
        setError(
          "Screen sharing is not supported by this browser."
        );

        return;
      }

      const peerConnection =
        peerConnectionRef.current;

      if (!peerConnection) {
        return;
      }

      try {
        if (
          isScreenSharing
        ) {
          const cameraStream =
            localStreamRef.current;

          if (!cameraStream) {
            return;
          }

          const cameraTrack =
            cameraStream.getVideoTracks()[0];

          if (!cameraTrack) {
            return;
          }

          const sender =
            peerConnection
              .getSenders()
              .find(
                (item) =>
                  item.track?.kind ===
                  "video"
              );

          if (sender) {
            await sender.replaceTrack(
              cameraTrack
            );
          }

          if (
            screenStreamRef.current
          ) {
            screenStreamRef.current
              .getTracks()
              .forEach(
                (track) => {
                  track.stop();
                }
              );

            screenStreamRef.current =
              null;
          }

          if (
            localVideoRef.current
          ) {
            localVideoRef.current.srcObject =
              cameraStream;
          }

          setIsScreenSharing(false);

          return;
        }

        const screenStream =
          await navigator.mediaDevices.getDisplayMedia(
            {
              video: true,
              audio: false,
            }
          );

        const screenTrack =
          screenStream.getVideoTracks()[0];

        if (!screenTrack) {
          return;
        }

        const sender =
          peerConnection
            .getSenders()
            .find(
              (item) =>
                item.track?.kind ===
                "video"
            );

        if (sender) {
          await sender.replaceTrack(
            screenTrack
          );
        }

        screenStreamRef.current =
          screenStream;

        if (
          localVideoRef.current
        ) {
          localVideoRef.current.srcObject =
            screenStream;
        }

        setIsScreenSharing(true);

        screenTrack.onended =
          () => {
            void toggleScreenShare();
          };
      } catch (err) {
        console.error(
          "Screen sharing failed:",
          err
        );

        setError(
          "Screen sharing was cancelled or unavailable."
        );
      }
    };

  useEffect(() => {
    if (
      !Number.isInteger(
        numericUserId
      ) ||
      numericUserId <= 0
    ) {
      setError(
        "Invalid student ID."
      );

      setCallState("error");

      return;
    }

    if (
      !Number.isInteger(
        conversationId
      ) ||
      conversationId <= 0
    ) {
      setError(
        "A valid conversation is required for the call."
      );

      setCallState("error");

      return;
    }

    if (!user?.id) {
      setError(
        "Your account information is unavailable. Please log in again."
      );

      setCallState("error");

      return;
    }

    remoteUserIdRef.current =
      numericUserId;

    let cancelled = false;

    const initialize = async () => {
      try {
        setError("");

        const profile =
          await studentService.getStudentProfile(
            numericUserId
          );

        if (cancelled) {
          return;
        }

        setStudent(profile);

        if (
          profile.userId === user.id
        ) {
          throw new Error(
            "You cannot call yourself."
          );
        }

        await callWebSocketService.connect();

        if (cancelled) {
          return;
        }

        callWebSocketService.addListener(
          handleIncomingSignal
        );

        /*
         * The caller starts the call.
         * The receiver waits for CALL_REQUEST.
         *
         * To make this page usable from both sides,
         * we start the outgoing call when the page
         * is opened with the requested mode and the
         * caller is the current user.
         *
         * The CALL_REQUEST itself tells the other
         * side which mode is being used.
         */

        if (
  isInitiator &&
  !callStartedRef.current
) {
  await startOutgoingCall();
}
      } catch (err) {
        console.error(
          "Failed to initialize call:",
          err
        );

        if (!cancelled) {
          setCallState("error");

          setError(
            err instanceof Error
              ? err.message
              : "Unable to initialize the call."
          );
        }
      }
    };

    void initialize();

    return () => {
      cancelled = true;

      callWebSocketService.removeListener(
        handleIncomingSignal
      );

      cleanupCall();
    };
  }, [
    cleanupCall,
    conversationId,
    handleIncomingSignal,
     isInitiator,
    numericUserId,
    startOutgoingCall,
    user?.id,
  ]);

  const goBackToMessage =
    () => {
      navigate(
        `/students/${numericUserId}/message`
      );
    };

  const statusText = (() => {
    switch (callState) {
      case "connecting":
        return "Connecting...";

      case "calling":
        return `Calling ${targetName}...`;

      case "ringing":
        return `${targetName} is calling...`;

      case "connected":
        return "Connected";

      case "rejected":
        return "Call rejected";

      case "ended":
        return "Call ended";

      case "error":
        return "Call error";

      default:
        return "";
    }
  })();

  const isCallActive =
    callState === "calling" ||
    callState === "ringing" ||
    callState === "connecting" ||
    callState === "connected";

  return (
    <div className="min-h-screen bg-slate-950 px-4 py-6 text-white">
      <div className="mx-auto flex min-h-[calc(100vh-3rem)] max-w-6xl flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-900 shadow-2xl">

        {/* Header */}

        <div className="flex items-center justify-between border-b border-slate-800 px-4 py-4 sm:px-6">

          <div className="flex min-w-0 items-center gap-3">

            <button
              type="button"
              onClick={
                goBackToMessage
              }
              className="rounded-xl p-2 text-slate-400 transition hover:bg-slate-800 hover:text-white"
            >
              ←
            </button>

            <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full bg-indigo-600 font-bold">
              {student?.profileImageUrl ? (
                <img
                  src={
                    student.profileImageUrl
                  }
                  alt={targetName}
                  className="h-full w-full object-cover"
                />
              ) : (
                targetName
                  .charAt(0)
                  .toUpperCase()
              )}
            </div>

            <div className="min-w-0">
              <h1 className="truncate text-base font-bold">
                {targetName}
              </h1>

              <p className="text-xs text-slate-400">
                {callMode === "video"
                  ? "Video Call"
                  : "Voice Call"}
              </p>
            </div>

          </div>

          <div className="text-right">
            <p className="text-sm font-medium text-slate-300">
              {statusText}
            </p>

            {callState ===
              "connected" && (
              <p className="text-xs text-emerald-400">
                Secure peer connection
              </p>
            )}
          </div>

        </div>

        {/* Main Call Area */}

        <div className="relative flex flex-1 items-center justify-center overflow-hidden bg-slate-950 p-4 sm:p-6">

          {/* Remote Video */}

          <div className="relative flex h-full min-h-[420px] w-full items-center justify-center overflow-hidden rounded-3xl bg-slate-900">

            {callMode ===
              "video" &&
            remoteStreamReady ? (
              <video
                ref={
                  remoteVideoRef
                }
                autoPlay
                playsInline
                className="h-full max-h-[70vh] w-full object-contain"
              />
            ) : (
              <div className="flex flex-col items-center justify-center text-center">

                <div className="mb-5 flex h-24 w-24 items-center justify-center overflow-hidden rounded-full bg-indigo-600 text-3xl font-bold shadow-xl">
                  {student?.profileImageUrl ? (
                    <img
                      src={
                        student.profileImageUrl
                      }
                      alt={targetName}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    targetName
                      .charAt(0)
                      .toUpperCase()
                  )}
                </div>

                <h2 className="text-xl font-bold">
                  {targetName}
                </h2>

                <p className="mt-2 text-sm text-slate-400">
                  {statusText}
                </p>

              </div>
            )}

            {/* Local Video */}

            {callMode ===
              "video" && (
              <div className="absolute bottom-5 right-5 h-32 w-48 overflow-hidden rounded-2xl border border-slate-700 bg-slate-800 shadow-xl sm:h-40 sm:w-56">

                <video
                  ref={
                    localVideoRef
                  }
                  autoPlay
                  muted
                  playsInline
                  className="h-full w-full object-cover"
                />

                {!localStreamRef.current && (
                  <div className="absolute inset-0 flex items-center justify-center text-xs text-slate-400">
                    Camera
                  </div>
                )}

              </div>
            )}

          </div>

          {/* Incoming Call */}

          {incomingCall && (
            <div className="absolute left-1/2 top-1/2 w-[calc(100%-2rem)] max-w-sm -translate-x-1/2 -translate-y-1/2 rounded-3xl border border-slate-700 bg-slate-900/95 p-6 text-center shadow-2xl backdrop-blur">

              <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-indigo-600 text-2xl">
                {callMode ===
                "video"
                  ? "📹"
                  : "📞"}
              </div>

              <h2 className="text-xl font-bold">
                Incoming{" "}
                {callMode ===
                "video"
                  ? "video"
                  : "voice"}{" "}
                call
              </h2>

              <p className="mt-2 text-sm text-slate-400">
                {targetName} is calling
                you.
              </p>

              <div className="mt-6 flex gap-3">

                <button
                  type="button"
                  onClick={() =>
                    void handleCallReject()
                  }
                  className="flex-1 rounded-xl bg-red-600 px-4 py-3 text-sm font-semibold transition hover:bg-red-700"
                >
                  Reject
                </button>

                <button
                  type="button"
                  onClick={() =>
                    void handleCallAccept()
                  }
                  className="flex-1 rounded-xl bg-emerald-600 px-4 py-3 text-sm font-semibold transition hover:bg-emerald-700"
                >
                  Accept
                </button>

              </div>

            </div>
          )}

        </div>

        {/* Error */}

        {error && (
          <div className="border-t border-red-900 bg-red-950/50 px-4 py-3 text-center text-sm text-red-300">
            {error}
          </div>
        )}

        {/* Controls */}

        <div className="border-t border-slate-800 bg-slate-900 px-4 py-5">

          <div className="flex flex-wrap items-center justify-center gap-3">

            {/* Microphone */}

            <button
              type="button"
              onClick={
                toggleMute
              }
              disabled={
                !isCallActive
              }
              className={`flex h-12 items-center gap-2 rounded-2xl px-4 text-sm font-semibold transition ${
                isMuted
                  ? "bg-red-600 hover:bg-red-700"
                  : "bg-slate-800 hover:bg-slate-700"
              } disabled:cursor-not-allowed disabled:opacity-40`}
            >
              {isMuted
                ? "🔇 Unmute"
                : "🎤 Mute"}
            </button>

            {/* Camera */}

            {callMode ===
              "video" && (
              <button
                type="button"
                onClick={
                  toggleCamera
                }
                disabled={
                  !isCallActive
                }
                className={`flex h-12 items-center gap-2 rounded-2xl px-4 text-sm font-semibold transition ${
                  !isCameraEnabled
                    ? "bg-red-600 hover:bg-red-700"
                    : "bg-slate-800 hover:bg-slate-700"
                } disabled:cursor-not-allowed disabled:opacity-40`}
              >
                {isCameraEnabled
                  ? "📷 Camera"
                  : "🚫 Camera"}
              </button>
            )}

            {/* Screen Share */}

            {callMode ===
              "video" && (
              <button
                type="button"
                onClick={() =>
                  void toggleScreenShare()
                }
                disabled={
                  callState !==
                  "connected"
                }
                className="flex h-12 items-center gap-2 rounded-2xl bg-slate-800 px-4 text-sm font-semibold transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-40"
              >
                🖥️{" "}
                {isScreenSharing
                  ? "Stop Share"
                  : "Share Screen"}
              </button>
            )}

            {/* End Call */}

            <button
              type="button"
              onClick={() =>
                void handleEndCall()
              }
              disabled={
                !isCallActive
              }
              className="flex h-12 items-center gap-2 rounded-2xl bg-red-600 px-5 text-sm font-semibold transition hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-40"
            >
              📵 End Call
            </button>

          </div>

        {!isCallActive && (
  <div className="mt-4 text-center">

    <button
      type="button"
      onClick={goBackToMessage}
      className="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold transition hover:bg-indigo-700"
    >
      Back to Message
    </button>

  </div>
)}

        </div>
      </div>
    </div>
  );
};

export default VideoCallPage;