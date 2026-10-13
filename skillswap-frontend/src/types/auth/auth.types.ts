export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  verificationMethod?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface VerifyOtpRequest {
  otp: string;
  method: string;
}

export interface ResendOtpRequest {
  method: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface AuthUser {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  role?: string;
  emailVerified?: boolean;
  phoneVerified?: boolean;
  accountVerified?: boolean;
}

/**
 * Response returned by:
 * POST /api/v1/auth/verify-otp
 *
 * Backend DTO:
 * AuthResponse
 */
export interface AuthResponse {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  verified: boolean;
  accessToken: string;
  refreshToken: string;
}

/**
 * Response returned by:
 * POST /api/v1/auth/login
 *
 * Backend DTO:
 * LoginResponse
 */
export interface LoginResponse {
  id: number;
  accountVerified: boolean | undefined;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  verified: boolean;
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresInSeconds: number;
  refreshTokenExpiresInSeconds: number;
}

export interface OtpResponse {
  message?: string;
  verificationMethod?: string;
  expiresInSeconds?: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse extends LoginResponse {}

export interface ForgotPasswordResponse {
  message?: string;
}

export interface ResetPasswordResponse {
  message?: string;
}