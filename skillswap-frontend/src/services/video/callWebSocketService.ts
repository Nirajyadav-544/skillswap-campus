import {
  Client,
  type IMessage,
  type StompSubscription,
} from "@stomp/stompjs";

import storageService from "../../utils/storage";

export type CallSignalType =
  | "CALL_REQUEST"
  | "CALL_ACCEPT"
  | "CALL_REJECT"
  | "OFFER"
  | "ANSWER"
  | "ICE_CANDIDATE"
  | "CALL_END";

export interface CallSignal {
  conversationId: number;
  senderUserId: number;
  targetUserId: number;
  type: CallSignalType;
  payload?: string | null;
}

export interface SendCallSignalRequest {
  conversationId: number;
  targetUserId: number;
  type: CallSignalType;
  payload?: string | null;
}

type CallSignalListener = (signal: CallSignal) => void;

class CallWebSocketService {
  private client: Client | null = null;

  private subscription: StompSubscription | null = null;

  private listeners = new Set<CallSignalListener>();

  private connecting = false;

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      const token = storageService.getAccessToken();

      if (!token) {
        reject(new Error("Access token is missing"));
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
              new Error("WebSocket connection failed")
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

        debug: () => {
          // Keep STOMP debug logs disabled.
        },

        onConnect: () => {
          this.connecting = false;
          this.client = client;

          this.subscribeToCalls();

          resolve();
        },

        onStompError: (frame) => {
          console.error(
            "Call WebSocket STOMP error:",
            frame.headers,
            frame.body
          );

          this.connecting = false;

          reject(
            new Error(
              frame.headers["message"] ||
                "Call WebSocket STOMP error"
            )
          );
        },

        onWebSocketError: (event) => {
          console.error(
            "Call WebSocket connection error:",
            event
          );
        },

        onWebSocketClose: () => {
          this.connecting = false;
          this.subscription = null;
        },
      });

      this.client = client;

      client.activate();
    });
  }

  private subscribeToCalls(): void {
    if (
      !this.client ||
      !this.client.connected
    ) {
      return;
    }

    if (this.subscription) {
      return;
    }

    this.subscription =
      this.client.subscribe(
        "/user/queue/calls",
        (message: IMessage) => {
          try {
            const signal =
              JSON.parse(
                message.body
              ) as CallSignal;

            this.listeners.forEach(
              (listener) => {
                listener(signal);
              }
            );
          } catch (error) {
            console.error(
              "Failed to parse call signal:",
              error
            );
          }
        }
      );
  }

  addListener(
    listener: CallSignalListener
  ): void {
    this.listeners.add(listener);
  }

  removeListener(
    listener: CallSignalListener
  ): void {
    this.listeners.delete(listener);
  }

  async sendSignal(
    request: SendCallSignalRequest
  ): Promise<void> {

    await this.connect();

    if (
      !this.client ||
      !this.client.connected
    ) {
      throw new Error(
        "Call WebSocket is not connected"
      );
    }

    this.client.publish({
      destination: "/app/call.signal",
      body: JSON.stringify(request),
    });
  }

  disconnect(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = null;
    }

    this.listeners.clear();

    if (this.client) {
      void this.client.deactivate();
    }

    this.client = null;
    this.connecting = false;
  }

  isConnected(): boolean {
    return this.client?.connected ?? false;
  }
}

const callWebSocketService =
  new CallWebSocketService();

export default callWebSocketService;