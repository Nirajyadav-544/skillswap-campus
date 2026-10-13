import {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { useNavigate, useParams } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import chatService, {
  type MessageSummary,
} from "../../services/chat/chatService";
import chatWebSocketService from "../../services/websocket/chatWebSocketService";
import studentService from "../../services/student/studentService";
import {
  getStudentName,
  type StudentProfile,
} from "../../types/student/student.types";

const MessagePage = () => {
  const { userId } = useParams<{ userId: string }>();

  const navigate = useNavigate();

  const { user } = useAuth();

  const [student, setStudent] =
    useState<StudentProfile | null>(null);

  const [messages, setMessages] =
    useState<MessageSummary[]>([]);

  const [conversationId, setConversationId] =
    useState<number | null>(null);

  const [messageText, setMessageText] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [sending, setSending] =
    useState(false);

  const [error, setError] =
    useState("");

  const messagesEndRef =
    useRef<HTMLDivElement | null>(null);

  const numericUserId =
    Number(userId);

  const targetName = useMemo(() => {
    if (!student) {
      return "Student";
    }

    return getStudentName(student);
  }, [student]);

  const scrollToBottom = useCallback(() => {
    window.setTimeout(() => {
      messagesEndRef.current?.scrollIntoView({
        behavior: "smooth",
      });
    }, 50);
  }, []);

  const addIncomingMessage = useCallback(
    (message: MessageSummary) => {
      setMessages((currentMessages) => {

        const alreadyExists =
          currentMessages.some(
            (currentMessage) =>
              currentMessage.id === message.id
          );

        if (alreadyExists) {
          return currentMessages;
        }

        return [
          ...currentMessages,
          message,
        ];
      });

      scrollToBottom();
    },
    [scrollToBottom]
  );

  useEffect(() => {
    if (
      !Number.isInteger(numericUserId) ||
      numericUserId <= 0
    ) {
      setError("Invalid student ID.");
      setLoading(false);
      return;
    }

    if (!user?.email) {
      setError(
        "Your account information is unavailable. Please log in again."
      );
      setLoading(false);
      return;
    }

    let cancelled = false;

    const loadConversation = async () => {
      try {
        setLoading(true);
        setError("");

        const studentProfile =
          await studentService.getStudentProfile(
            numericUserId
          );

        if (cancelled) {
          return;
        }

        setStudent(studentProfile);

        if (
          studentProfile.userId === user.id
        ) {
          throw new Error(
            "You cannot message yourself."
          );
        }

        const conversation =
          await chatService.createOrGetDirectConversation(
            numericUserId,
            user.email
          );

        if (cancelled) {
          return;
        }

        setConversationId(conversation.id);

        const existingMessages =
          await chatService.getMessages(
            conversation.id,
            user.email
          );

        if (cancelled) {
          return;
        }

        setMessages(existingMessages);

        await chatWebSocketService.subscribe(
          conversation.id,
          addIncomingMessage
        );

        if (!cancelled) {
          scrollToBottom();
        }
      } catch (err) {
        console.error(
          "Failed to load message conversation:",
          err
        );

        if (!cancelled) {
          setError(
            err instanceof Error
              ? err.message
              : "Unable to open the conversation."
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void loadConversation();

    return () => {
      cancelled = true;

      if (conversationId !== null) {
        chatWebSocketService.unsubscribe(
          conversationId,
          addIncomingMessage
        );
      }
    };
  }, [
    numericUserId,
    user,
    addIncomingMessage,
    scrollToBottom,
  ]);

  const handleSend = async () => {
    const trimmedMessage =
      messageText.trim();

    if (
      !trimmedMessage ||
      conversationId === null ||
      sending
    ) {
      return;
    }

    try {
      setSending(true);
      setError("");

   await chatWebSocketService.sendMessage({
  conversationId,
  type: "TEXT",
  content: trimmedMessage,
});

      setMessageText("");
    } catch (err) {
      console.error(
        "Failed to send message:",
        err
      );

      setError(
        "Unable to send the message. Please try again."
      );
    } finally {
      setSending(false);
    }
  };

  const handleKeyDown = (
    event: React.KeyboardEvent<HTMLInputElement>
  ) => {
    if (
      event.key === "Enter" &&
      !event.shiftKey
    ) {
      event.preventDefault();

      void handleSend();
    }
  };

  const handleVoiceCall = () => {
    if (conversationId === null) {
      return;
    }

    navigate(
      `/students/${numericUserId}/video-call?conversationId=${conversationId}&mode=voice`
    );
  };

  const handleVideoCall = () => {
    if (conversationId === null) {
      return;
    }

    navigate(
      `/students/${numericUserId}/video-call?conversationId=${conversationId}&mode=video`
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50">
        <div className="mx-auto flex min-h-screen max-w-5xl items-center justify-center px-4">
          <div className="text-center">
            <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

            <p className="text-sm font-medium text-slate-600">
              Opening conversation...
            </p>
          </div>
        </div>
      </div>
    );
  }

  if (error && !student) {
    return (
      <div className="min-h-screen bg-slate-50 px-4 py-10">
        <div className="mx-auto max-w-3xl rounded-2xl border border-red-200 bg-white p-8 text-center shadow-sm">
          <div className="mb-4 text-4xl">
            ⚠️
          </div>

          <h1 className="text-xl font-bold text-slate-900">
            Unable to open conversation
          </h1>

          <p className="mt-2 text-sm text-red-600">
            {error}
          </p>

          <button
            type="button"
            onClick={() =>
              navigate(
                `/students/${numericUserId}`
              )
            }
            className="mt-6 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
          >
            Back to Profile
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-6">
      <div className="mx-auto flex h-[calc(100vh-3rem)] max-w-5xl flex-col overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">

        {/* Header */}

        <div className="flex items-center justify-between border-b border-slate-200 px-4 py-4 sm:px-6">

          <div className="flex min-w-0 items-center gap-3">

            <button
              type="button"
              onClick={() =>
                navigate(
                  `/students/${numericUserId}`
                )
              }
              className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-900"
              aria-label="Back"
            >
              ←
            </button>

            <div className="flex h-11 w-11 shrink-0 items-center justify-center overflow-hidden rounded-full bg-indigo-100 font-bold text-indigo-700">
              {student?.profileImageUrl ? (
                <img
                  src={student.profileImageUrl}
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
              <h1 className="truncate text-base font-bold text-slate-900">
                {targetName}
              </h1>

              <p className="truncate text-xs text-slate-500">
                {student?.headline ||
                  "SkillSwap student"}
              </p>
            </div>

          </div>

          {/* Call Actions */}

          <div className="flex items-center gap-2">

            <button
              type="button"
              onClick={handleVoiceCall}
              disabled={
                conversationId === null
              }
              className="rounded-xl border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              title="Voice call"
            >
              <span className="sm:hidden">
                📞
              </span>

              <span className="hidden sm:inline">
                📞 Voice
              </span>
            </button>

            <button
              type="button"
              onClick={handleVideoCall}
              disabled={
                conversationId === null
              }
              className="rounded-xl bg-indigo-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
              title="Video call"
            >
              <span className="sm:hidden">
                📹
              </span>

              <span className="hidden sm:inline">
                📹 Video
              </span>
            </button>

          </div>
        </div>

        {/* Error */}

        {error && (
          <div className="border-b border-red-100 bg-red-50 px-4 py-2 text-sm text-red-600">
            {error}
          </div>
        )}

        {/* Messages */}

        <div className="flex-1 overflow-y-auto bg-slate-50 px-4 py-5 sm:px-6">

          {messages.length === 0 ? (
            <div className="flex h-full items-center justify-center">
              <div className="max-w-sm text-center">
                <div className="mb-3 text-4xl">
                  💬
                </div>

                <h2 className="font-bold text-slate-900">
                  Start the conversation
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Send a message to{" "}
                  {targetName} and start
                  connecting.
                </p>
              </div>
            </div>
          ) : (
            <div className="space-y-3">

              {messages.map((message) => {

                const isOwnMessage =
                  message.senderUserId ===
                  user?.id;

                return (
                  <div
                    key={message.id}
                    className={`flex ${
                      isOwnMessage
                        ? "justify-end"
                        : "justify-start"
                    }`}
                  >
                    <div
                      className={`max-w-[80%] rounded-2xl px-4 py-3 sm:max-w-[65%] ${
                        isOwnMessage
                          ? "rounded-br-md bg-indigo-600 text-white"
                          : "rounded-bl-md bg-white text-slate-900 shadow-sm ring-1 ring-slate-200"
                      }`}
                    >
                      {message.type &&
                      message.type !==
                        "TEXT" ? (
                        <div className="mb-1 text-xs font-semibold opacity-70">
                          {message.type}
                        </div>
                      ) : null}

                      {message.content && (
                        <p className="whitespace-pre-wrap break-words text-sm">
                          {message.content}
                        </p>
                      )}

                      {message.attachmentName && (
                        <div className="mt-2 rounded-lg bg-black/10 px-3 py-2 text-xs">
                          📎{" "}
                          {message.attachmentName}
                        </div>
                      )}

                      <div
                        className={`mt-1 text-[10px] ${
                          isOwnMessage
                            ? "text-indigo-100"
                            : "text-slate-400"
                        }`}
                      >
                        {message.createdAt
                          ? new Date(
                              message.createdAt
                            ).toLocaleTimeString(
                              [],
                              {
                                hour: "2-digit",
                                minute:
                                  "2-digit",
                              }
                            )
                          : ""}
                      </div>
                    </div>
                  </div>
                );
              })}

              <div
                ref={messagesEndRef}
              />
            </div>
          )}

        </div>

        {/* Composer */}

        <div className="border-t border-slate-200 bg-white p-3 sm:p-4">

          <div className="flex items-center gap-2">

            <input
              type="text"
              value={messageText}
              onChange={(event) =>
                setMessageText(
                  event.target.value
                )
              }
              onKeyDown={handleKeyDown}
              placeholder={`Message ${targetName}...`}
              maxLength={10000}
              disabled={
                conversationId === null ||
                sending
              }
              className="min-w-0 flex-1 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:opacity-60"
            />

            <button
              type="button"
              onClick={() =>
                void handleSend()
              }
              disabled={
                !messageText.trim() ||
                conversationId === null ||
                sending
              }
              className="rounded-2xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {sending
                ? "..."
                : "Send"}
            </button>

          </div>

        </div>
      </div>
    </div>
  );
};

export default MessagePage;