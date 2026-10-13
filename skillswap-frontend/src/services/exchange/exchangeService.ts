import apiClient from "../api/apiClient";
import storageService from "../../utils/storage";

export type ExchangeType =
  | "ONE_WAY"
  | "SKILL_SWAP"
  | "COLLABORATION";

export type ExchangeStatus =
  | "PENDING"
  | "ACCEPTED"
  | "REJECTED"
  | "CANCELLED"
  | "EXPIRED"
  | "COMPLETED"
  | "ARCHIVED";

export type ExchangeResponseStatus =
  | "PENDING"
  | "ACCEPTED"
  | "REJECTED"
  | "CANCELLED";

export interface CreateExchangeRequest {
  recipientUserId: number;
  exchangeType: ExchangeType;
  requestMessage?: string;
  requestedSkillName?: string;
  offeredSkillName?: string;
  matchResultId?: number;
}

export interface UpdateExchangeRequest {
  exchangeType?: ExchangeType;
  requestMessage?: string;
  requestedSkillName?: string;
  offeredSkillName?: string;
}

export interface ExchangeRequestResponse {
  id: number;

  requesterUserId: number;
  requesterEmail: string;
  requesterName: string | null;

  recipientUserId: number;
  recipientEmail: string;
  recipientName: string | null;

  matchResultId: number | null;

  exchangeType: ExchangeType;
  status: ExchangeStatus;

  requestMessage: string | null;
  responseMessage: string | null;

  requestedSkillName: string | null;
  offeredSkillName: string | null;

  createdAt: string;
  respondedAt: string | null;
  acceptedAt: string | null;
  rejectedAt: string | null;
  cancelledAt: string | null;
  completedAt: string | null;
  expiresAt: string | null;
  updatedAt: string | null;

  pending: boolean;
  accepted: boolean;
  rejected: boolean;
  cancelled: boolean;
  completed: boolean;
  expired: boolean;
  closed: boolean;
}

export interface ExchangeSummaryResponse {
  id: number;

  requesterUserId: number;
  requesterName: string | null;

  recipientUserId: number;
  recipientName: string | null;

  exchangeType: ExchangeType;
  status: ExchangeStatus;

  requestedSkillName: string | null;
  offeredSkillName: string | null;

  createdAt: string;
  expiresAt: string | null;

  pending: boolean;
  accepted: boolean;
  completed: boolean;
  closed: boolean;
}

export interface ExchangePageResponse {
  content: ExchangeSummaryResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ExchangeOfferRequest {
  skillName: string;
  skillId?: number;
  offerDescription?: string;
}

export interface ExchangeOfferResponse {
  id: number;

  exchangeRequestId: number;

  userId: number;
  userEmail: string;
  userName: string | null;

  skillName: string;
  skillId: number | null;
  offerDescription: string | null;

  responseStatus: ExchangeResponseStatus;

  createdAt: string;
  respondedAt: string | null;
  updatedAt: string | null;

  pending: boolean;
  accepted: boolean;
  rejected: boolean;
  cancelled: boolean;
}

export interface ExchangeOfferPageResponse {
  content: ExchangeOfferResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

const getToken = (): string | undefined => {
  return storageService.getAccessToken() ?? undefined;
};

const exchangeService = {
  // =========================================================
  // EXCHANGE REQUESTS
  // =========================================================

  createRequest(
    request: CreateExchangeRequest
  ): Promise<ExchangeRequestResponse> {
    return apiClient.post<ExchangeRequestResponse>(
      "/v1/exchange/requests",
      request,
      getToken()
    );
  },

  getRequestById(
    requestId: number
  ): Promise<ExchangeRequestResponse> {
    return apiClient.get<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}`,
      getToken()
    );
  },

  getSentRequests(): Promise<ExchangeRequestResponse[]> {
    return apiClient.get<ExchangeRequestResponse[]>(
      "/v1/exchange/requests/sent",
      getToken()
    );
  },

  getReceivedRequests(): Promise<ExchangeRequestResponse[]> {
    return apiClient.get<ExchangeRequestResponse[]>(
      "/v1/exchange/requests/received",
      getToken()
    );
  },

  getPendingRequests(): Promise<ExchangeRequestResponse[]> {
    return apiClient.get<ExchangeRequestResponse[]>(
      "/v1/exchange/requests/pending",
      getToken()
    );
  },

  getSentRequestsPage(
    page = 0,
    size = 20
  ): Promise<ExchangePageResponse> {
    return apiClient.get<ExchangePageResponse>(
      `/v1/exchange/requests/sent/page?page=${page}&size=${size}`,
      getToken()
    );
  },

  getReceivedRequestsPage(
    page = 0,
    size = 20
  ): Promise<ExchangePageResponse> {
    return apiClient.get<ExchangePageResponse>(
      `/v1/exchange/requests/received/page?page=${page}&size=${size}`,
      getToken()
    );
  },

  getSentRequestsByStatus(
    status: ExchangeStatus
  ): Promise<ExchangeRequestResponse[]> {
    return apiClient.get<ExchangeRequestResponse[]>(
      `/v1/exchange/requests/sent/status/${status}`,
      getToken()
    );
  },

  getReceivedRequestsByStatus(
    status: ExchangeStatus
  ): Promise<ExchangeRequestResponse[]> {
    return apiClient.get<ExchangeRequestResponse[]>(
      `/v1/exchange/requests/received/status/${status}`,
      getToken()
    );
  },

  updateRequest(
    requestId: number,
    request: UpdateExchangeRequest
  ): Promise<ExchangeRequestResponse> {
    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}`,
      request,
      getToken()
    );
  },

  acceptRequest(
    requestId: number,
    responseMessage?: string
  ): Promise<ExchangeRequestResponse> {
    const query = responseMessage?.trim()
      ? `?responseMessage=${encodeURIComponent(
          responseMessage.trim()
        )}`
      : "";

    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}/accept${query}`,
      undefined,
      getToken()
    );
  },

  rejectRequest(
    requestId: number,
    responseMessage?: string
  ): Promise<ExchangeRequestResponse> {
    const query = responseMessage?.trim()
      ? `?responseMessage=${encodeURIComponent(
          responseMessage.trim()
        )}`
      : "";

    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}/reject${query}`,
      undefined,
      getToken()
    );
  },

  cancelRequest(
    requestId: number
  ): Promise<ExchangeRequestResponse> {
    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}/cancel`,
      undefined,
      getToken()
    );
  },

  completeRequest(
    requestId: number
  ): Promise<ExchangeRequestResponse> {
    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}/complete`,
      undefined,
      getToken()
    );
  },

  archiveRequest(
    requestId: number
  ): Promise<ExchangeRequestResponse> {
    return apiClient.patch<ExchangeRequestResponse>(
      `/v1/exchange/requests/${requestId}/archive`,
      undefined,
      getToken()
    );
  },

  deleteRequest(
    requestId: number
  ): Promise<void> {
    return apiClient.delete<void>(
      `/v1/exchange/requests/${requestId}`,
      getToken()
    );
  },

  countSentRequests(): Promise<number> {
    return apiClient.get<number>(
      "/v1/exchange/requests/count/sent",
      getToken()
    );
  },

  countReceivedRequests(): Promise<number> {
    return apiClient.get<number>(
      "/v1/exchange/requests/count/received",
      getToken()
    );
  },

  countSentRequestsByStatus(
    status: ExchangeStatus
  ): Promise<number> {
    return apiClient.get<number>(
      `/v1/exchange/requests/count/sent/${status}`,
      getToken()
    );
  },

  countReceivedRequestsByStatus(
    status: ExchangeStatus
  ): Promise<number> {
    return apiClient.get<number>(
      `/v1/exchange/requests/count/received/${status}`,
      getToken()
    );
  },

  // =========================================================
  // EXCHANGE OFFERS
  // =========================================================

  createOffer(
    exchangeRequestId: number,
    request: ExchangeOfferRequest
  ): Promise<ExchangeOfferResponse> {
    return apiClient.post<ExchangeOfferResponse>(
      `/v1/exchange/offers/request/${exchangeRequestId}`,
      request,
      getToken()
    );
  },

  getOfferById(
    offerId: number
  ): Promise<ExchangeOfferResponse> {
    return apiClient.get<ExchangeOfferResponse>(
      `/v1/exchange/offers/${offerId}`,
      getToken()
    );
  },

  getOffersForExchange(
    exchangeRequestId: number
  ): Promise<ExchangeOfferResponse[]> {
    return apiClient.get<ExchangeOfferResponse[]>(
      `/v1/exchange/offers/request/${exchangeRequestId}`,
      getToken()
    );
  },

  getOffersForExchangePage(
    exchangeRequestId: number,
    page = 0,
    size = 20
  ): Promise<ExchangeOfferPageResponse> {
    return apiClient.get<ExchangeOfferPageResponse>(
      `/v1/exchange/offers/request/${exchangeRequestId}/page?page=${page}&size=${size}`,
      getToken()
    );
  },

  getMyOffers(): Promise<ExchangeOfferResponse[]> {
    return apiClient.get<ExchangeOfferResponse[]>(
      "/v1/exchange/offers/mine",
      getToken()
    );
  },

  getMyOffersPage(
    page = 0,
    size = 20
  ): Promise<ExchangeOfferPageResponse> {
    return apiClient.get<ExchangeOfferPageResponse>(
      `/v1/exchange/offers/mine/page?page=${page}&size=${size}`,
      getToken()
    );
  },

  getMyOffersByStatus(
    status: ExchangeResponseStatus
  ): Promise<ExchangeOfferResponse[]> {
    return apiClient.get<ExchangeOfferResponse[]>(
      `/v1/exchange/offers/mine/status/${status}`,
      getToken()
    );
  },

  acceptOffer(
    offerId: number
  ): Promise<ExchangeOfferResponse> {
    return apiClient.patch<ExchangeOfferResponse>(
      `/v1/exchange/offers/${offerId}/accept`,
      undefined,
      getToken()
    );
  },

  rejectOffer(
    offerId: number
  ): Promise<ExchangeOfferResponse> {
    return apiClient.patch<ExchangeOfferResponse>(
      `/v1/exchange/offers/${offerId}/reject`,
      undefined,
      getToken()
    );
  },

  cancelOffer(
    offerId: number
  ): Promise<ExchangeOfferResponse> {
    return apiClient.patch<ExchangeOfferResponse>(
      `/v1/exchange/offers/${offerId}/cancel`,
      undefined,
      getToken()
    );
  },

  deleteOffer(
    offerId: number
  ): Promise<void> {
    return apiClient.delete<void>(
      `/v1/exchange/offers/${offerId}`,
      getToken()
    );
  },

  countOffersForExchange(
    exchangeRequestId: number
  ): Promise<number> {
    return apiClient.get<number>(
      `/v1/exchange/offers/request/${exchangeRequestId}/count`,
      getToken()
    );
  },

  countMyOffers(): Promise<number> {
    return apiClient.get<number>(
      "/v1/exchange/offers/mine/count",
      getToken()
    );
  },
};

export default exchangeService;