import apiClient from "../api/apiClient";

export interface ConversationSummary {
  id: number;
  type?: string;
  title?: string;
  description?: string;
  referenceType?: string;
  referenceId?: number;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface MessageSummary {
  id: number;
  conversationId: number;
  senderUserId: number;
  senderEmail?: string;
  type?: string;
  status?: string;
  content?: string;

  attachmentUrl?: string | null;
  attachmentName?: string | null;
  attachmentType?: string | null;

  replyToMessageId?: number | null;
  createdAt?: string;
  editedAt?: string | null;
}

export interface CreateDirectConversationResponse {
  id: number;
  type?: string;
  title?: string;
  description?: string;
  referenceType?: string;
  referenceId?: number;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface UploadedFileResponse {
  fileUrl: string;
  originalFileName: string;
  contentType: string;
  fileSize: number;
}

const chatService = {
  async createOrGetDirectConversation(
    targetUserId: number,
    userEmail: string
  ): Promise<CreateDirectConversationResponse> {
    return apiClient.post<CreateDirectConversationResponse>(
      `/v1/chat/conversations/direct?targetUserId=${encodeURIComponent(
        targetUserId
      )}&userEmail=${encodeURIComponent(userEmail)}`
    );
  },

  async getMyConversations(
    userEmail: string
  ): Promise<ConversationSummary[]> {
    return apiClient.get<ConversationSummary[]>(
      `/v1/chat/conversations?userEmail=${encodeURIComponent(userEmail)}`
    );
  },

  async getMessages(
    conversationId: number,
    userEmail: string
  ): Promise<MessageSummary[]> {
    return apiClient.get<MessageSummary[]>(
      `/v1/chat/conversations/${conversationId}/messages?userEmail=${encodeURIComponent(
        userEmail
      )}`
    );
  },

  async sendMessage(
    conversationId: number,
    userEmail: string,
    content: string
  ): Promise<MessageSummary> {
    return apiClient.post<MessageSummary>(
      `/v1/chat/conversations/${conversationId}/messages?userEmail=${encodeURIComponent(
        userEmail
      )}`,
      {
        type: "TEXT",
        content,
      }
    );
  },

  async uploadFile(
    file: File
  ): Promise<UploadedFileResponse> {
    const formData = new FormData();

    formData.append("file", file);

    return apiClient.post<UploadedFileResponse>(
      "/v1/files/upload",
      formData
    );
  },

  async sendFileMessage(
    conversationId: number,
    userEmail: string,
    fileUrl: string,
    fileName: string,
    fileType: string
  ): Promise<MessageSummary> {
    return apiClient.post<MessageSummary>(
      `/v1/chat/conversations/${conversationId}/messages?userEmail=${encodeURIComponent(
        userEmail
      )}`,
      {
        type: "FILE",
        attachmentUrl: fileUrl,
        attachmentName: fileName,
        attachmentType: fileType,
      }
    );
  },
};

export default chatService;