import environment from "../../config/environment";
import storageService from "../../utils/storage";
import { ApiError } from "../api/apiError";

export interface FileUploadResponse {
  fileId: number | null;
  originalFileName: string;
  fileUrl: string;
  contentType: string;
  fileSize: number;
  extension: string;
  message: string;
}

const fileService = {
  async uploadFile(file: File): Promise<FileUploadResponse> {
    const token = storageService.getAccessToken();

    if (!token) {
      throw new ApiError({
        status: 401,
        message: "Access token is missing",
      });
    }

    const formData = new FormData();
    formData.append("file", file);

    let response: Response;

    try {
      response = await fetch(
        `${environment.apiBaseUrl}/v1/files/upload`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
          body: formData,
        }
      );
    } catch (error) {
      const message =
        error instanceof Error
          ? error.message
          : "Unable to connect to the server.";

      throw new ApiError({
        status: 0,
        message: `Network error: ${message}`,
      });
    }

    if (!response.ok) {
      let errorBody: unknown = null;

      const contentType =
        response.headers.get("content-type");

      if (contentType?.includes("application/json")) {
        try {
          errorBody = await response.json();
        } catch {
          errorBody = null;
        }
      } else {
        try {
          errorBody = await response.text();
        } catch {
          errorBody = null;
        }
      }

      let message = `File upload failed with status ${response.status}.`;

      if (
        errorBody &&
        typeof errorBody === "object"
      ) {
        const body = errorBody as Record<string, unknown>;

        if (
          typeof body.message === "string" &&
          body.message.trim()
        ) {
          message = body.message;
        } else if (
          typeof body.error === "string" &&
          body.error.trim()
        ) {
          message = body.error;
        }
      } else if (
        typeof errorBody === "string" &&
        errorBody.trim()
      ) {
        message = errorBody;
      }

      throw new ApiError({
        status: response.status,
        message,
        body: errorBody,
      });
    }

    return response.json() as Promise<FileUploadResponse>;
  },
};

export default fileService;