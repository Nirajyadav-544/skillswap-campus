export interface ApiErrorData {
  status: number;
  message: string;
  body?: unknown;
}

export class ApiError extends Error {
  public readonly status: number;
  public readonly body?: unknown;

  constructor(data: ApiErrorData) {
    super(data.message);

    this.name = "ApiError";
    this.status = data.status;
    this.body = data.body;

    Object.setPrototypeOf(this, ApiError.prototype);
  }
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError;
}

export function getApiErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Something went wrong. Please try again.";
}