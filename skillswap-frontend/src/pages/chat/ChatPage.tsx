import {
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import type {
  ChangeEvent,
  FormEvent,
} from "react";
import { Link } from "react-router-dom";

import AppShell from "../../components/layout/AppShell";
import { useAuth } from "../../hooks/useAuth";

import chatService from "../../services/chat/chatService";
import type {
  ConversationSummary,
  MessageSummary,
} from "../../services/chat/chatService";

import fileService from "../../services/file/fileService";

import chatWebSocketService from "../../services/websocket/chatWebSocketService";

const FILE_BASE_URL = "http://localhost:8080";

const ChatPage = () => {
  const { user } = useAuth();

  const [conversations, setConversations] = useState<
    ConversationSummary[]
  >([]);

  const [selectedConversationId, setSelectedConversationId] =
    useState<number | null>(null);

  const [messages, setMessages] = useState<MessageSummary[]>([]);
  const [messageText, setMessageText] = useState("");

  const [loadingConversations, setLoadingConversations] =
    useState(true);

  const [loadingMessages, setLoadingMessages] =
    useState(false);

  const [sending, setSending] = useState(false);

  const [uploadingFile, setUploadingFile] =
    useState(false);

  const [showAttachmentMenu, setShowAttachmentMenu] =
    useState(false);

  const [conversationError, setConversationError] =
    useState<string | null>(null);

  const [messageError, setMessageError] =
    useState<string | null>(null);

  const mediaInputRef =
    useRef<HTMLInputElement | null>(null);

  const documentInputRef =
    useRef<HTMLInputElement | null>(null);

  const audioInputRef =
    useRef<HTMLInputElement | null>(null);

  const selectedConversation = useMemo(
    () =>
      conversations.find(
        (conversation) =>
          conversation.id === selectedConversationId
      ) ?? null,
    [conversations, selectedConversationId]
  );

  /*
   * ============================================================
   * LOAD CONVERSATIONS
   * ============================================================
   */

  const loadConversations = async () => {
    if (!user?.email) {
      return;
    }

    try {
      setLoadingConversations(true);
      setConversationError(null);

      const response =
        await chatService.getMyConversations(
          user.email
        );

      setConversations(response);

      if (
        response.length > 0 &&
        !response.some(
          (conversation) =>
            conversation.id === selectedConversationId
        )
      ) {
        setSelectedConversationId(
          response[0].id
        );
      }
    } catch (error) {
      console.error(
        "Failed to load conversations:",
        error
      );

      setConversationError(
        "Unable to load your conversations. Please try again."
      );
    } finally {
      setLoadingConversations(false);
    }
  };

  /*
   * ============================================================
   * LOAD MESSAGES
   * ============================================================
   */

  const loadMessages = async (
    conversationId: number
  ) => {
    if (!user?.email) {
      return;
    }

    try {
      setLoadingMessages(true);
      setMessageError(null);

      const response =
        await chatService.getMessages(
          conversationId,
          user.email
        );

      setMessages(response);
    } catch (error) {
      console.error(
        "Failed to load messages:",
        error
      );

      setMessages([]);

      setMessageError(
        "Unable to load messages for this conversation."
      );
    } finally {
      setLoadingMessages(false);
    }
  };

  useEffect(() => {
    loadConversations();
  }, [user?.email]);

  useEffect(() => {
    if (selectedConversationId === null) {
      setMessages([]);
      return;
    }

    loadMessages(selectedConversationId);
  }, [
    selectedConversationId,
    user?.email,
  ]);

  /*
   * ============================================================
   * REAL-TIME WEBSOCKET SUBSCRIPTION
   * ============================================================
   */

  useEffect(() => {
    if (selectedConversationId === null) {
      return;
    }

    let active = true;

    const handleRealtimeMessage = (
      incomingMessage: MessageSummary
    ) => {
      if (!active) {
        return;
      }

      setMessages((currentMessages) => {
        const alreadyExists =
          currentMessages.some(
            (message) =>
              message.id ===
              incomingMessage.id
          );

        if (alreadyExists) {
          return currentMessages;
        }

        return [
          ...currentMessages,
          incomingMessage,
        ];
      });
    };

    chatWebSocketService
      .subscribe(
        selectedConversationId,
        handleRealtimeMessage
      )
      .catch((error) => {
        console.error(
          "Failed to subscribe to conversation:",
          error
        );

        if (active) {
          setMessageError(
            "Real-time connection could not be established."
          );
        }
      });

    return () => {
      active = false;

      chatWebSocketService.unsubscribe(
        selectedConversationId,
        handleRealtimeMessage
      );
    };
  }, [selectedConversationId]);

  /*
   * ============================================================
   * SEND TEXT MESSAGE
   * ============================================================
   */

  const handleSendMessage = async (
    event: FormEvent
  ) => {
    event.preventDefault();

    if (
      !user?.email ||
      selectedConversationId === null
    ) {
      return;
    }

    const content = messageText.trim();

    if (!content || sending || uploadingFile) {
      return;
    }

    try {
      setSending(true);
      setMessageError(null);

      await chatWebSocketService.sendMessage({
        conversationId:
          selectedConversationId,
        type: "TEXT",
        content,
      });

      /*
       * Do not manually add the message.
       * Backend broadcasts the saved message.
       */
      setMessageText("");
    } catch (error) {
      console.error(
        "Failed to send message:",
        error
      );

      setMessageError(
        "Message could not be sent. Please try again."
      );
    } finally {
      setSending(false);
    }
  };

  /*
   * ============================================================
   * FILE / IMAGE / VIDEO / AUDIO UPLOAD
   * ============================================================
   */

  const handleFileSelected = async (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    const file =
      event.target.files?.[0];

    /*
     * Allows selecting the same file again later.
     */
    event.target.value = "";

    if (
      !file ||
      selectedConversationId === null ||
      uploadingFile
    ) {
      return;
    }

    try {
      setUploadingFile(true);
      setMessageError(null);
      setShowAttachmentMenu(false);

      /*
       * Step 1:
       * Upload physical file to backend.
       */
      const uploaded =
        await fileService.uploadFile(file);

      /*
       * Prevent a silent failure if backend
       * does not return a file URL.
       */
      if (!uploaded.fileUrl?.trim()) {
        throw new Error(
          "File was uploaded, but the server did not return a file URL."
        );
      }

      /*
       * Step 2:
       * Determine chat message type
       * from uploaded MIME type.
       */
      let messageType:
        | "IMAGE"
        | "VIDEO"
        | "AUDIO"
        | "FILE" = "FILE";

      if (
        uploaded.contentType.startsWith(
          "image/"
        )
      ) {
        messageType = "IMAGE";
      } else if (
        uploaded.contentType.startsWith(
          "video/"
        )
      ) {
        messageType = "VIDEO";
      } else if (
        uploaded.contentType.startsWith(
          "audio/"
        )
      ) {
        messageType = "AUDIO";
      }

      /*
       * Step 3:
       * Send attachment information through
       * WebSocket.
       */
      await chatWebSocketService.sendMessage({
        conversationId:
          selectedConversationId,

        type: messageType,

        attachmentUrl:
          uploaded.fileUrl,

        attachmentName:
          uploaded.originalFileName,

        attachmentType:
          uploaded.contentType,
      });
    } catch (error) {
      console.error(
        "Failed to upload attachment:",
        error
      );

      setMessageError(
        error instanceof Error
          ? error.message
          : "Attachment could not be sent."
      );
    } finally {
      setUploadingFile(false);
    }
  };

  /*
   * ============================================================
   * ATTACHMENT MENU
   * ============================================================
   */

  const openMediaPicker = () => {
    setShowAttachmentMenu(false);
    mediaInputRef.current?.click();
  };

  const openDocumentPicker = () => {
    setShowAttachmentMenu(false);
    documentInputRef.current?.click();
  };

  const openAudioPicker = () => {
    setShowAttachmentMenu(false);
    audioInputRef.current?.click();
  };

  /*
   * ============================================================
   * CONVERSATION HELPERS
   * ============================================================
   */

  const getConversationTitle = (
    conversation: ConversationSummary
  ) => {
    if (conversation.title?.trim()) {
      return conversation.title;
    }

    if (
      conversation.type === "DIRECT"
    ) {
      return "Direct Conversation";
    }

    return conversation.type
      ? `${conversation.type} Conversation`
      : "Conversation";
  };

  const getConversationSubtitle = (
    conversation: ConversationSummary
  ) => {
    if (
      conversation.description?.trim()
    ) {
      return conversation.description;
    }

    if (
      conversation.type === "DIRECT"
    ) {
      return "Direct student conversation";
    }

    return "SkillSwap Campus conversation";
  };

  const formatTime = (
    date?: string
  ) => {
    if (!date) {
      return "";
    }

    const parsedDate =
      new Date(date);

    if (
      Number.isNaN(
        parsedDate.getTime()
      )
    ) {
      return "";
    }

    return parsedDate.toLocaleTimeString(
      [],
      {
        hour: "numeric",
        minute: "2-digit",
      }
    );
  };

  const formatDate = (
    date?: string
  ) => {
    if (!date) {
      return "";
    }

    const parsedDate =
      new Date(date);

    if (
      Number.isNaN(
        parsedDate.getTime()
      )
    ) {
      return "";
    }

    return parsedDate.toLocaleDateString(
      [],
      {
        day: "numeric",
        month: "short",
        year: "numeric",
      }
    );
  };

  /*
   * ============================================================
   * ATTACHMENT URL
   * ============================================================
   */

  const getAttachmentUrl = (
    attachmentUrl?: string | null
  ) => {
    if (!attachmentUrl) {
      return "";
    }

    if (
      attachmentUrl.startsWith(
        "http://"
      ) ||
      attachmentUrl.startsWith(
        "https://"
      )
    ) {
      return attachmentUrl;
    }

    return `${FILE_BASE_URL}${attachmentUrl}`;
  };

  return (
    <AppShell>
      <main className="min-h-[calc(100vh-136px)] bg-slate-50">
        <div className="mx-auto flex max-w-7xl flex-col px-4 py-6 sm:px-6 lg:px-8">

          {/* =====================================================
              PAGE HEADER
          ====================================================== */}

          <div className="mb-6">
            <Link
              to="/dashboard"
              className="text-sm font-semibold text-indigo-600 hover:text-indigo-700"
            >
              ← Back to dashboard
            </Link>

            <div className="mt-4">
              <h1 className="text-3xl font-black tracking-tight text-slate-950">
                Messages
              </h1>

              <p className="mt-2 text-sm leading-6 text-slate-500">
                Connect with students, continue conversations and build
                meaningful learning relationships.
              </p>
            </div>
          </div>

          <section className="grid min-h-[650px] overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm lg:grid-cols-[340px_1fr]">

            {/* =====================================================
                CONVERSATION SIDEBAR
            ====================================================== */}

            <aside className="border-b border-slate-200 lg:border-b-0 lg:border-r">
              <div className="border-b border-slate-200 px-5 py-5">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-base font-extrabold text-slate-900">
                      Conversations
                    </h2>

                    <p className="mt-1 text-xs text-slate-500">
                      {conversations.length} conversation
                      {conversations.length === 1
                        ? ""
                        : "s"}
                    </p>
                  </div>

                  <button
                    type="button"
                    onClick={
                      loadConversations
                    }
                    disabled={
                      loadingConversations
                    }
                    className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-bold text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    Refresh
                  </button>
                </div>
              </div>

              <div className="max-h-[570px] overflow-y-auto">
                {loadingConversations ? (
                  <div className="space-y-3 p-5">
                    {[1, 2, 3].map(
                      (item) => (
                        <div
                          key={item}
                          className="animate-pulse rounded-2xl border border-slate-100 p-4"
                        >
                          <div className="h-10 w-10 rounded-full bg-slate-200" />

                          <div className="mt-3 h-3 w-32 rounded bg-slate-200" />

                          <div className="mt-2 h-2 w-44 rounded bg-slate-100" />
                        </div>
                      )
                    )}
                  </div>
                ) : conversationError ? (
                  <div className="p-5">
                    <div className="rounded-2xl border border-red-100 bg-red-50 p-4">
                      <p className="text-sm font-semibold text-red-700">
                        {conversationError}
                      </p>

                      <button
                        type="button"
                        onClick={
                          loadConversations
                        }
                        className="mt-3 text-xs font-bold text-red-700 underline"
                      >
                        Try again
                      </button>
                    </div>
                  </div>
                ) : conversations.length === 0 ? (
                  <div className="flex min-h-[400px] flex-col items-center justify-center px-6 text-center">
                    <div className="grid h-16 w-16 place-items-center rounded-2xl bg-indigo-50 text-2xl text-indigo-600">
                      💬
                    </div>

                    <h3 className="mt-5 text-base font-extrabold text-slate-900">
                      No conversations yet
                    </h3>

                    <p className="mt-2 max-w-xs text-sm leading-6 text-slate-500">
                      Discover students and start a direct conversation
                      with someone you want to connect with.
                    </p>

                    <Link
                      to="/matching"
                      className="mt-5 rounded-xl bg-indigo-600 px-4 py-2.5 text-xs font-bold text-white hover:bg-indigo-700"
                    >
                      Discover Students
                    </Link>
                  </div>
                ) : (
                  <div className="p-3">
                    {conversations.map(
                      (conversation) => {
                        const active =
                          conversation.id ===
                          selectedConversationId;

                        return (
                          <button
                            key={
                              conversation.id
                            }
                            type="button"
                            onClick={() =>
                              setSelectedConversationId(
                                conversation.id
                              )
                            }
                            className={`mb-2 flex w-full items-start gap-3 rounded-2xl p-4 text-left transition ${
                              active
                                ? "bg-indigo-50 ring-1 ring-indigo-100"
                                : "hover:bg-slate-50"
                            }`}
                          >
                            <div
                              className={`grid h-11 w-11 shrink-0 place-items-center rounded-full text-sm font-black ${
                                active
                                  ? "bg-indigo-600 text-white"
                                  : "bg-slate-100 text-slate-600"
                              }`}
                            >
                              {getConversationTitle(
                                conversation
                              )
                                .charAt(
                                  0
                                )
                                .toUpperCase()}
                            </div>

                            <div className="min-w-0 flex-1">
                              <div className="flex items-center justify-between gap-2">
                                <strong className="truncate text-sm font-extrabold text-slate-900">
                                  {getConversationTitle(
                                    conversation
                                  )}
                                </strong>

                                <span className="shrink-0 text-[10px] font-semibold uppercase text-slate-400">
                                  {conversation.type ??
                                    "CHAT"}
                                </span>
                              </div>

                              <p className="mt-1 truncate text-xs text-slate-500">
                                {getConversationSubtitle(
                                  conversation
                                )}
                              </p>

                              <p className="mt-2 text-[10px] text-slate-400">
                                {formatDate(
                                  conversation.updatedAt
                                )}
                              </p>
                            </div>
                          </button>
                        );
                      }
                    )}
                  </div>
                )}
              </div>
            </aside>

            {/* =====================================================
                MESSAGE AREA
            ====================================================== */}

            <section className="flex min-h-[650px] flex-col">
              {selectedConversation ? (
                <>
                  {/* =================================================
                      CHAT HEADER
                  ================================================== */}

                  <header className="flex items-center justify-between border-b border-slate-200 px-5 py-4 sm:px-6">
                    <div className="flex min-w-0 items-center gap-3">
                      <div className="grid h-11 w-11 shrink-0 place-items-center rounded-full bg-indigo-100 font-black text-indigo-600">
                        {getConversationTitle(
                          selectedConversation
                        )
                          .charAt(
                            0
                          )
                          .toUpperCase()}
                      </div>

                      <div className="min-w-0">
                        <h2 className="truncate text-sm font-extrabold text-slate-900 sm:text-base">
                          {getConversationTitle(
                            selectedConversation
                          )}
                        </h2>

                        <p className="truncate text-xs text-slate-500">
                          {getConversationSubtitle(
                            selectedConversation
                          )}
                        </p>
                      </div>
                    </div>

                    <span className="hidden rounded-full bg-emerald-50 px-3 py-1.5 text-[10px] font-bold uppercase tracking-wide text-emerald-600 sm:inline-flex">
                      Real-time chat
                    </span>
                  </header>

                  {/* =================================================
                      MESSAGES
                  ================================================== */}

                  <div className="flex-1 overflow-y-auto bg-slate-50 px-4 py-6 sm:px-6">
                    {loadingMessages ? (
                      <div className="flex h-full items-center justify-center">
                        <div className="text-center">
                          <div className="mx-auto h-8 w-8 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />

                          <p className="mt-3 text-sm font-semibold text-slate-500">
                            Loading messages...
                          </p>
                        </div>
                      </div>
                    ) : messageError ? (
                      <div className="flex h-full items-center justify-center">
                        <div className="rounded-2xl border border-red-100 bg-red-50 p-5 text-center">
                          <p className="text-sm font-semibold text-red-700">
                            {messageError}
                          </p>

                          <button
                            type="button"
                            onClick={() =>
                              loadMessages(
                                selectedConversation.id
                              )
                            }
                            className="mt-3 text-xs font-bold text-red-700 underline"
                          >
                            Try again
                          </button>
                        </div>
                      </div>
                    ) : messages.length === 0 ? (
                      <div className="flex h-full items-center justify-center">
                        <div className="max-w-sm text-center">
                          <div className="mx-auto grid h-16 w-16 place-items-center rounded-2xl bg-white text-2xl shadow-sm">
                            👋
                          </div>

                          <h3 className="mt-5 text-base font-extrabold text-slate-900">
                            Start the conversation
                          </h3>

                          <p className="mt-2 text-sm leading-6 text-slate-500">
                            Send the first message and start connecting
                            with this student.
                          </p>
                        </div>
                      </div>
                    ) : (
                      <div className="space-y-3">
                        {messages.map(
                          (message) => {
                            const isMine =
                              message.senderUserId ===
                              user?.id;

                            const attachmentUrl =
                              getAttachmentUrl(
                                message.attachmentUrl
                              );

                            return (
                              <div
                                key={
                                  message.id
                                }
                                className={`flex ${
                                  isMine
                                    ? "justify-end"
                                    : "justify-start"
                                }`}
                              >
                                <div className="max-w-[85%] sm:max-w-[70%]">
                                  {!isMine && (
                                    <p className="mb-1 px-1 text-[10px] font-semibold text-slate-400">
                                      {message.senderEmail ??
                                        "Student"}
                                    </p>
                                  )}

                                  <div
                                    className={`rounded-2xl px-4 py-3 shadow-sm ${
                                      isMine
                                        ? "rounded-br-md bg-indigo-600 text-white"
                                        : "rounded-bl-md border border-slate-200 bg-white text-slate-800"
                                    }`}
                                  >

                                    {/* ==========================
                                        TEXT CONTENT
                                    =========================== */}

                                    {message.content && (
                                      <p className="whitespace-pre-wrap break-words text-sm leading-6">
                                        {
                                          message.content
                                        }
                                      </p>
                                    )}

                                    {/* ==========================
                                        IMAGE
                                    =========================== */}

                                    {message.attachmentUrl &&
                                      message.type ===
                                        "IMAGE" && (
                                        <div
                                          className={
                                            message.content
                                              ? "mt-2"
                                              : ""
                                          }
                                        >
                                          <a
                                            href={
                                              attachmentUrl
                                            }
                                            target="_blank"
                                            rel="noreferrer"
                                            className="block overflow-hidden rounded-xl"
                                          >
                                            <img
                                              src={
                                                attachmentUrl
                                              }
                                              alt={
                                                message.attachmentName ??
                                                "Image attachment"
                                              }
                                              className="max-h-80 w-full object-cover"
                                            />
                                          </a>

                                          {message.attachmentName && (
                                            <p className="mt-1 truncate text-[10px] opacity-70">
                                              {
                                                message.attachmentName
                                              }
                                            </p>
                                          )}
                                        </div>
                                      )}

                                    {/* ==========================
                                        VIDEO
                                    =========================== */}

                                    {message.attachmentUrl &&
                                      message.type ===
                                        "VIDEO" && (
                                        <div
                                          className={
                                            message.content
                                              ? "mt-2"
                                              : ""
                                          }
                                        >
                                          <video
                                            controls
                                            preload="metadata"
                                            className="max-h-80 w-full rounded-xl"
                                            src={
                                              attachmentUrl
                                            }
                                          />

                                          {message.attachmentName && (
                                            <p className="mt-1 truncate text-[10px] opacity-70">
                                              {
                                                message.attachmentName
                                              }
                                            </p>
                                          )}
                                        </div>
                                      )}

                                    {/* ==========================
                                        AUDIO
                                    =========================== */}

                                    {message.attachmentUrl &&
                                      message.type ===
                                        "AUDIO" && (
                                        <div
                                          className={
                                            message.content
                                              ? "mt-2"
                                              : ""
                                          }
                                        >
                                          <audio
                                            controls
                                            className="w-full"
                                            src={
                                              attachmentUrl
                                            }
                                          />

                                          {message.attachmentName && (
                                            <p className="mt-1 truncate text-[10px] opacity-70">
                                              {
                                                message.attachmentName
                                              }
                                            </p>
                                          )}
                                        </div>
                                      )}

                                    {/* ==========================
                                        FILE / DOCUMENT
                                    =========================== */}

                                    {message.attachmentUrl &&
                                      message.type ===
                                        "FILE" && (
                                        <a
                                          href={
                                            attachmentUrl
                                          }
                                          target="_blank"
                                          rel="noreferrer"
                                          download={
                                            message.attachmentName ??
                                            undefined
                                          }
                                          className={`mt-2 flex items-center gap-3 rounded-xl px-3 py-3 text-sm transition ${
                                            isMine
                                              ? "bg-white/10 hover:bg-white/20"
                                              : "bg-slate-100 hover:bg-slate-200"
                                          }`}
                                        >
                                          <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-white/20 text-xl">
                                            📄
                                          </span>

                                          <span className="min-w-0 flex-1">
                                            <span className="block truncate font-semibold">
                                              {message.attachmentName ??
                                                "Document"}
                                            </span>

                                            <span className="text-xs opacity-70">
                                              Open / Download
                                            </span>
                                          </span>

                                          <span className="text-lg opacity-70">
                                            ↓
                                          </span>
                                        </a>
                                      )}

                                    {/* ==========================
                                        FALLBACK ATTACHMENT
                                    =========================== */}

                                    {message.attachmentUrl &&
                                      message.type !==
                                        "IMAGE" &&
                                      message.type !==
                                        "VIDEO" &&
                                      message.type !==
                                        "AUDIO" &&
                                      message.type !==
                                        "FILE" && (
                                        <a
                                          href={
                                            attachmentUrl
                                          }
                                          target="_blank"
                                          rel="noreferrer"
                                          download={
                                            message.attachmentName ??
                                            undefined
                                          }
                                          className={`mt-2 flex items-center gap-3 rounded-xl px-3 py-3 text-sm ${
                                            isMine
                                              ? "bg-white/10 hover:bg-white/20"
                                              : "bg-slate-100 hover:bg-slate-200"
                                          }`}
                                        >
                                          <span className="text-xl">
                                            📎
                                          </span>

                                          <span className="min-w-0 flex-1">
                                            <span className="block truncate font-semibold">
                                              {message.attachmentName ??
                                                "Attachment"}
                                            </span>

                                            <span className="text-xs opacity-70">
                                              Open / Download
                                            </span>
                                          </span>
                                        </a>
                                      )}

                                    {/* ==========================
                                        MESSAGE TIME
                                    =========================== */}

                                    <div
                                      className={`mt-1 text-[10px] ${
                                        isMine
                                          ? "text-indigo-200"
                                          : "text-slate-400"
                                      }`}
                                    >
                                      {formatTime(
                                        message.createdAt
                                      )}

                                      {message.editedAt &&
                                        " • edited"}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            );
                          }
                        )}
                      </div>
                    )}
                  </div>


                </>
              ) : (
                <div className="flex flex-1 items-center justify-center px-6">
                  <div className="max-w-md text-center">
                    <div className="mx-auto grid h-20 w-20 place-items-center rounded-3xl bg-indigo-50 text-3xl">
                      💬
                    </div>

                    <h2 className="mt-6 text-xl font-black text-slate-950">
                      Your conversations
                    </h2>

                    <p className="mt-3 text-sm leading-7 text-slate-500">
                      Select a conversation from the left to view messages
                      and continue talking with another student.
                    </p>
                  </div>
                </div>
              )}

                  <form
                    onSubmit={
                      handleSendMessage
                    }
                    className="border-t border-slate-200 bg-white p-4 sm:p-5"
                  >
                    {/* =================================================
                        HIDDEN FILE INPUTS
                    ================================================== */}

                    {/* Photo / Video */}

                    <input
                      ref={mediaInputRef}
                      type="file"
                      className="hidden"
                      accept="image/*,video/*"
                      onChange={
                        handleFileSelected
                      }
                    />

                    {/* Documents */}

                    <input
                      ref={documentInputRef}
                      type="file"
                      className="hidden"
                      accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt"
                      onChange={
                        handleFileSelected
                      }
                    />

                    {/* Audio */}

                    <input
                      ref={audioInputRef}
                      type="file"
                      className="hidden"
                      accept="audio/*"
                      onChange={
                        handleFileSelected
                      }
                    />

                    <div className="relative flex items-end gap-3">

                      {/* =================================================
                          ATTACHMENT MENU
                      ================================================== */}

                      {showAttachmentMenu && (
                        <div className="absolute bottom-16 left-0 z-30 w-64 overflow-hidden rounded-2xl border border-slate-200 bg-white p-2 shadow-xl">

                          {/* Photo / Video */}

                          <button
                            type="button"
                            onClick={
                              openMediaPicker
                            }
                            disabled={
                              uploadingFile ||
                              sending
                            }
                            className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-indigo-50 text-xl">
                              🖼️
                            </span>

                            <span className="min-w-0">
                              <span className="block text-sm font-bold text-slate-800">
                                Photo / Video
                              </span>

                              <span className="block text-xs text-slate-400">
                                Images and videos
                              </span>
                            </span>
                          </button>

                          {/* Document */}

                          <button
                            type="button"
                            onClick={
                              openDocumentPicker
                            }
                            disabled={
                              uploadingFile ||
                              sending
                            }
                            className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-amber-50 text-xl">
                              📄
                            </span>

                            <span className="min-w-0">
                              <span className="block text-sm font-bold text-slate-800">
                                Document
                              </span>

                              <span className="block truncate text-xs text-slate-400">
                                PDF, Word, PPT, Excel, TXT
                              </span>
                            </span>
                          </button>

                          {/* Audio */}

                          <button
                            type="button"
                            onClick={
                              openAudioPicker
                            }
                            disabled={
                              uploadingFile ||
                              sending
                            }
                            className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-emerald-50 text-xl">
                              🎵
                            </span>

                            <span className="min-w-0">
                              <span className="block text-sm font-bold text-slate-800">
                                Audio
                              </span>

                              <span className="block text-xs text-slate-400">
                                MP3, WAV, OGG
                              </span>
                            </span>
                          </button>
                        </div>
                      )}

                      {/* =================================================
                          ATTACHMENT BUTTON
                      ================================================== */}

                      <button
                        type="button"
                        onClick={() =>
                          setShowAttachmentMenu(
                            (current) =>
                              !current
                          )
                        }
                        disabled={
                          selectedConversationId === null ||
                          uploadingFile ||
                          sending
                        }
                        className={`grid h-[52px] w-[52px] shrink-0 place-items-center rounded-2xl border transition disabled:cursor-not-allowed disabled:opacity-50 ${
                          showAttachmentMenu
                            ? "border-indigo-300 bg-indigo-50 text-indigo-600"
                            : "border-slate-200 bg-slate-50 text-xl hover:bg-slate-100"
                        }`}
                        aria-label="Attach file"
                        title="Attach file"
                      >
                        {uploadingFile
                          ? "…"
                          : "📎"}
                      </button>

                      {/* =================================================
                          MESSAGE INPUT
                      ================================================== */}

                      <textarea
                        value={messageText}
                        onChange={(event) =>
                          setMessageText(
                            event.target.value
                          )
                        }
                        onKeyDown={(event) => {
                          if (
                            event.key ===
                              "Escape" &&
                            showAttachmentMenu
                          ) {
                            setShowAttachmentMenu(
                              false
                            );
                            return;
                          }

                          if (
                            event.key ===
                              "Enter" &&
                            !event.shiftKey
                          ) {
                            event.preventDefault();

                            if (
                              !sending &&
                              !uploadingFile
                            ) {
                              event.currentTarget.form?.requestSubmit();
                            }
                          }
                        }}
                        rows={2}
                        maxLength={10000}
                        placeholder={
                          uploadingFile
                            ? "Uploading attachment..."
                            : "Write a message..."
                        }
                        disabled={
                          selectedConversationId === null ||
                          uploadingFile
                        }
                        className="min-h-[52px] flex-1 resize-none rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60"
                      />

                      {/* =================================================
                          SEND BUTTON
                      ================================================== */}

                      <button
                        type="submit"
                        disabled={
                          selectedConversationId === null ||
                          !messageText.trim() ||
                          sending ||
                          uploadingFile
                        }
                        className="grid h-[52px] w-[52px] shrink-0 place-items-center rounded-2xl bg-indigo-600 text-lg font-bold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
                        aria-label="Send message"
                        title="Send message"
                      >
                        {sending
                          ? "…"
                          : "➤"}
                      </button>
                    </div>

                    {/* =================================================
                        COMPOSER FOOTER
                    ================================================== */}

                    <div className="mt-2 flex items-center justify-between">
                      <span className="text-[10px] text-slate-400">
                        {uploadingFile
                          ? "Uploading attachment..."
                          : "📎 Attach • Enter to send • Shift + Enter for new line"}
                      </span>

                      <span className="text-[10px] text-slate-400">
                        {messageText.length}/10000
                      </span>
                    </div>
                  </form>
            </section>
          </section>
        </div>
      </main>
    </AppShell>
  );
};

export default ChatPage;