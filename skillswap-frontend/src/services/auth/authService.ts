import apiClient from "../api/apiClient";

import type {
  AuthResponse,
  ForgotPasswordRequest,
  ForgotPasswordResponse,
  LoginRequest,
  LoginResponse,
  OtpResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  RegisterRequest,
  ResendOtpRequest,
  ResetPasswordRequest,
  ResetPasswordResponse,
  VerifyOtpRequest,
} from "../../types/auth/auth.types";

class AuthService {
  async register(
    request: RegisterRequest
  ): Promise<OtpResponse> {
    return apiClient.post<OtpResponse>(
      "/v1/auth/register",
      request
    );
  }

  async verifyOtp(
    email: string,
    request: VerifyOtpRequest
  ): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>(
      `/v1/auth/verify-otp?email=${encodeURIComponent(email)}`,
      request
    );
  }

  async resendOtp(
    email: string,
    request: ResendOtpRequest
  ): Promise<OtpResponse> {
    return apiClient.post<OtpResponse>(
      `/v1/auth/resend-otp?email=${encodeURIComponent(email)}`,
      request
    );
  }

  async login(
    request: LoginRequest
  ): Promise<LoginResponse> {
    return apiClient.post<LoginResponse>(
      "/v1/auth/login",
      request
    );
  }

  async refreshToken(
    request: RefreshTokenRequest
  ): Promise<RefreshTokenResponse> {
    return apiClient.post<RefreshTokenResponse>(
      "/v1/auth/refresh",
      request
    );
  }

  async logout(
    request: RefreshTokenRequest
  ): Promise<void> {
    await apiClient.post<void>(
      "/v1/auth/logout",
      request
    );
  }

  async logoutAll(): Promise<void> {
    await apiClient.post<void>(
      "/v1/auth/logout-all"
    );
  }

  async forgotPassword(
    request: ForgotPasswordRequest
  ): Promise<ForgotPasswordResponse> {
    await apiClient.post<void>(
      "/v1/auth/forgot-password",
      request
    );

    return {
      message:
        "If an account exists with this email, password reset instructions have been sent.",
    };
  }

  async verifyResetOtp(
    email: string,
    otp: string
  ): Promise<{
    message: string;
    resetToken: string;
    expiresInSeconds: number;
  }> {
    return apiClient.post<{
      message: string;
      resetToken: string;
      expiresInSeconds: number;
    }>(
      `/v1/auth/verify-reset-otp?email=${encodeURIComponent(email)}`,
      {
        otp,
        method: "EMAIL",
      }
    );
  }

  async resetPassword(
    request: ResetPasswordRequest
  ): Promise<ResetPasswordResponse> {
    await apiClient.post<void>(
      "/v1/auth/reset-password",
      request
    );

    return {
      message: "Password reset successfully.",
    };
  }
}

const authService = new AuthService();

export default authService;

