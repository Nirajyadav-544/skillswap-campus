
import environment from "../../config/environment";
import storageService from "../../utils/storage";

import { ApiError } from "./apiError";

/* =========================================================
   API REQUEST OPTIONS
========================================================= */

export interface ApiRequestOptions
  extends RequestInit {
  token?: string;
}

/* =========================================================
   API CLIENT
========================================================= */

class ApiClient {
  private readonly baseUrl: string;

  constructor() {
    this.baseUrl =
      environment.apiBaseUrl;
  }

  /* =======================================================
     URL BUILDER
  ======================================================= */

  private buildUrl(
    endpoint: string
  ): string {
    /*
     * Allow absolute URLs when required.
     */

    if (
      endpoint.startsWith("http://") ||
      endpoint.startsWith("https://")
    ) {
      return endpoint;
    }

    const normalizedBaseUrl =
      this.baseUrl.replace(/\/+$/, "");

    const normalizedEndpoint =
      endpoint.startsWith("/")
        ? endpoint
        : `/${endpoint}`;

    return `${normalizedBaseUrl}${normalizedEndpoint}`;
  }

  /* =======================================================
     REQUEST
  ======================================================= */

  async request<T>(
    endpoint: string,
    options: ApiRequestOptions = {}
  ): Promise<T> {
    const {
      token,
      headers,
      ...requestOptions
    } = options;

    const requestHeaders =
      new Headers(headers);

    /*
     * JSON content type is automatically added
     * when a request body exists.
     */

    if (
      !requestHeaders.has("Content-Type") &&
      requestOptions.body
    ) {
      requestHeaders.set(
        "Content-Type",
        "application/json"
      );
    }

    /* =====================================================
       AUTHORIZATION HEADER
    ===================================================== */

    /*
     * If a token is explicitly supplied, use it.
     *
     * Otherwise automatically read the access token
     * saved by AuthContext/storageService.
     */

    const accessToken =
      token ?? storageService.getAccessToken();

    if (accessToken) {
      requestHeaders.set(
        "Authorization",
        `Bearer ${accessToken}`
      );
    }

    /*
     * Standard JSON accept header.
     */

    if (
      !requestHeaders.has("Accept")
    ) {
      requestHeaders.set(
        "Accept",
        "application/json"
      );
    }

    let response: Response;

    try {
      response = await fetch(
        this.buildUrl(endpoint),
        {
          ...requestOptions,
          headers: requestHeaders,
        }
      );
    } catch (error) {
      /*
       * Network-level failure.
       */

      const message =
        error instanceof Error
          ? error.message
          : "Unable to connect to the server.";

      throw new ApiError({
        status: 0,
        message:
          `Network error: ${message}`,
      });
    }

    /* =====================================================
       HANDLE NON-SUCCESS RESPONSES
    ===================================================== */

    if (!response.ok) {
      let errorBody: unknown = null;

      const contentType =
        response.headers.get(
          "content-type"
        );

      /*
       * Try JSON first.
       */

      if (
        contentType?.includes(
          "application/json"
        )
      ) {
        try {
          errorBody =
            await response.json();
        } catch {
          errorBody = null;
        }
      } else {
        /*
         * Some Spring Boot errors can
         * arrive as plain text.
         */

        try {
          errorBody =
            await response.text();
        } catch {
          errorBody = null;
        }
      }

      const message =
        this.extractErrorMessage(
          errorBody,
          response.status
        );

      throw new ApiError({
        status: response.status,
        message,
        body: errorBody,
      });
    }

    /* =====================================================
       204 NO CONTENT
    ===================================================== */

    if (
      response.status === 204
    ) {
      return undefined as T;
    }

    /* =====================================================
       READ RESPONSE
    ===================================================== */

    const contentType =
      response.headers.get(
        "content-type"
      );

    if (
      contentType?.includes(
        "application/json"
      )
    ) {
      return response.json() as Promise<T>;
    }

    /*
     * For text responses.
     */

    return (await response.text()) as T;
  }

  /* =======================================================
     ERROR MESSAGE EXTRACTION
  ======================================================= */

  private extractErrorMessage(
    body: unknown,
    status: number
  ): string {
    if (
      body &&
      typeof body === "object"
    ) {
      const data =
        body as Record<
          string,
          unknown
        >;

      const possibleMessages = [
        data.message,
        data.error,
        data.detail,
      ];

      for (
        const possibleMessage of
          possibleMessages
      ) {
        if (
          typeof possibleMessage ===
            "string" &&
          possibleMessage.trim()
        ) {
          return possibleMessage;
        }
      }

      /*
       * Spring validation errors
       * may contain fieldErrors/errors.
       */

      if (
        Array.isArray(
          data.errors
        )
      ) {
        const messages =
          data.errors
            .map((item) => {
              if (
                item &&
                typeof item ===
                  "object"
              ) {
                const error =
                  item as Record<
                    string,
                    unknown
                  >;

                if (
                  typeof error.defaultMessage ===
                  "string"
                ) {
                  return error.defaultMessage;
                }

                if (
                  typeof error.message ===
                  "string"
                ) {
                  return error.message;
                }
              }

              return null;
            })
            .filter(
              (
                item
              ): item is string =>
                Boolean(item)
            );

        if (messages.length) {
          return messages.join(" ");
        }
      }
    }

    if (
      typeof body === "string" &&
      body.trim()
    ) {
      return body;
    }

    return `Request failed with status ${status}.`;
  }

  /* =======================================================
     GET
  ======================================================= */

  get<T>(
    endpoint: string,
    token?: string
  ): Promise<T> {
    return this.request<T>(
      endpoint,
      {
        method: "GET",
        token,
      }
    );
  }

  /* =======================================================
     POST
  ======================================================= */

  post<T>(
    endpoint: string,
    body?: unknown,
    token?: string
  ): Promise<T> {
    return this.request<T>(
      endpoint,
      {
        method: "POST",
        body:
          body !== undefined
            ? JSON.stringify(body)
            : undefined,
        token,
      }
    );
  }

  /* =======================================================
     PUT
  ======================================================= */

  put<T>(
    endpoint: string,
    body?: unknown,
    token?: string
  ): Promise<T> {
    return this.request<T>(
      endpoint,
      {
        method: "PUT",
        body:
          body !== undefined
            ? JSON.stringify(body)
            : undefined,
        token,
      }
    );
  }

  /* =======================================================
     PATCH
  ======================================================= */

  patch<T>(
    endpoint: string,
    body?: unknown,
    token?: string
  ): Promise<T> {
    return this.request<T>(
      endpoint,
      {
        method: "PATCH",
        body:
          body !== undefined
            ? JSON.stringify(body)
            : undefined,
        token,
      }
    );
  }

  /* =======================================================
     DELETE
  ======================================================= */

  delete<T>(
    endpoint: string,
    token?: string
  ): Promise<T> {
    return this.request<T>(
      endpoint,
      {
        method: "DELETE",
        token,
      }
    );
  }
}

/* =========================================================
   SINGLE API CLIENT INSTANCE
========================================================= */

const apiClient =
  new ApiClient();

export default apiClient;

