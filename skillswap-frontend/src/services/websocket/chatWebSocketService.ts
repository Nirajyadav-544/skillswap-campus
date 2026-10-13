import {
  Client,
  type IMessage,
  type StompSubscription,
} from "@stomp/stompjs";

import storageService from "../../utils/storage";

export type ChatMessageType =
  | "TEXT"
  | "IMAGE"
  | "FILE"
  | "VIDEO"
  | "AUDIO"
  | "LINK"
  | "SYSTEM";

export interface WebSocketMessage {
  id: number;
  conversationId: number;
  senderUserId: number;
  senderEmail?: string;
  type?: ChatMessageType;
  status?: string;
  content?: string;

  attachmentUrl?: string | null;
  attachmentName?: string | null;
  attachmentType?: string | null;

  replyToMessageId?: number | null;
  createdAt?: string;
  editedAt?: string | null;
}

export interface WebSocketSendMessageRequest {
  conversationId: number;
  type: ChatMessageType;
  content?: string;
  attachmentUrl?: string | null;
  attachmentName?: string | null;
  attachmentType?: string | null;
  replyToMessageId?: number | null;
}

type MessageListener = (
  message: WebSocketMessage
) => void;

class ChatWebSocketService {
  private client: Client | null = null;

  private subscriptions = new Map<
    number,
    StompSubscription
  >();

  private listeners = new Map<
    number,
    Set<MessageListener>
  >();

  private connecting = false;

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      const token = storageService.getAccessToken();

      if (!token) {
        reject(
          new Error("Access token is missing")
        );
        return;
      }

      if (this.client?.connected) {
        resolve();
        return;
      }

      if (this.connecting) {
        const waitForConnection = () => {
          if (this.client?.connected) {
            resolve();
            return;
          }

          if (!this.connecting) {
            reject(
              new Error(
                "WebSocket connection failed"
              )
            );
            return;
          }

          window.setTimeout(
            waitForConnection,
            100
          );
        };

        waitForConnection();
        return;
      }

      this.connecting = true;

      const client = new Client({
        brokerURL: "ws://localhost:8080/ws",

        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },

        reconnectDelay: 5000,

        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,

        debug: () => {},

        onConnect: () => {
          this.connecting = false;
          this.client = client;
          resolve();
        },

        onStompError: (frame) => {
          console.error(
            "WebSocket STOMP error:",
            frame.headers,
            frame.body
          );

          this.connecting = false;

          reject(
            new Error(
              frame.headers["message"] ||
                "WebSocket STOMP connection error"
            )
          );
        },

        onWebSocketError: (event) => {
          console.error(
            "WebSocket connection error:",
            event
          );
        },

        onWebSocketClose: () => {
          this.connecting = false;
        },
      });

      this.client = client;

      client.activate();
    });
  }

  async subscribe(
    conversationId: number,
    listener: MessageListener
  ): Promise<void> {
    await this.connect();

    if (!this.client?.connected) {
      throw new Error(
        "WebSocket is not connected"
      );
    }

    let conversationListeners =
      this.listeners.get(conversationId);

    if (!conversationListeners) {
      conversationListeners =
        new Set<MessageListener>();

      this.listeners.set(
        conversationId,
        conversationListeners
      );
    }

    conversationListeners.add(listener);

    if (
      this.subscriptions.has(
        conversationId
      )
    ) {
      return;
    }

    const subscription =
      this.client.subscribe(
        `/topic/conversations/${conversationId}`,
        (message: IMessage) => {
          try {
            const parsedMessage =
              JSON.parse(
                message.body
              ) as WebSocketMessage;

            const listeners =
              this.listeners.get(
                conversationId
              );

            if (!listeners) {
              return;
            }

            listeners.forEach(
              (currentListener) => {
                currentListener(
                  parsedMessage
                );
              }
            );
          } catch (error) {
            console.error(
              "Failed to parse WebSocket message:",
              error
            );
          }
        }
      );

    this.subscriptions.set(
      conversationId,
      subscription
    );
  }

  unsubscribe(
    conversationId: number,
    listener?: MessageListener
  ): void {
    const conversationListeners =
      this.listeners.get(
        conversationId
      );

    if (
      listener &&
      conversationListeners
    ) {
      conversationListeners.delete(
        listener
      );
    }

    if (
      !listener ||
      !conversationListeners ||
      conversationListeners.size === 0
    ) {
      const subscription =
        this.subscriptions.get(
          conversationId
        );

      if (subscription) {
        subscription.unsubscribe();
      }

      this.subscriptions.delete(
        conversationId
      );

      this.listeners.delete(
        conversationId
      );
    }
  }

  async sendMessage(
    request: WebSocketSendMessageRequest
  ): Promise<void> {
    await this.connect();

    if (!this.client?.connected) {
      throw new Error(
        "WebSocket is not connected"
      );
    }

    const normalizedContent =
      request.content?.trim() || undefined;

    const normalizedAttachmentUrl =
      request.attachmentUrl?.trim() ||
      undefined;

    if (
      !normalizedContent &&
      !normalizedAttachmentUrl
    ) {
      return;
    }

    const payload: WebSocketSendMessageRequest = {
      conversationId:
        request.conversationId,
      type: request.type,
      content: normalizedContent,
      attachmentUrl:
        normalizedAttachmentUrl,
      attachmentName:
        request.attachmentName?.trim() ||
        undefined,
      attachmentType:
        request.attachmentType?.trim() ||
        undefined,
      replyToMessageId:
        request.replyToMessageId ?? null,
    };

    this.client.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(payload),
    });
  }

  disconnect(): void {
    this.subscriptions.forEach(
      (subscription) => {
        subscription.unsubscribe();
      }
    );

    this.subscriptions.clear();
    this.listeners.clear();

    if (this.client) {
      void this.client.deactivate();
    }

    this.client = null;
    this.connecting = false;
  }

  isConnected(): boolean {
    return (
      this.client?.connected ?? false
    );
  }
}

const chatWebSocketService =
  new ChatWebSocketService();

export default chatWebSocketService;