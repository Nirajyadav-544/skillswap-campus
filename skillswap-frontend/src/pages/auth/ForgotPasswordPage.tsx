
import { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import authService from "../../services/auth/authService";
import { getApiErrorMessage } from "../../services/api/apiError";

const ForgotPasswordPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const initialEmail =
    (location.state as { email?: string } | null)?.email ?? "";

  const [email, setEmail] = useState(initialEmail);
  const [otp, setOtp] = useState("");

  const [resetToken, setResetToken] = useState("");

  const [step, setStep] = useState<
    "email" | "otp" | "password"
  >("email");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleEmailChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    setEmail(event.target.value);
    setError("");
    setSuccess("");
  };

  const handleOtpChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    const value = event.target.value
      .replace(/\D/g, "")
      .slice(0, 6);

    setOtp(value);
    setError("");
    setSuccess("");
  };

  const handleSendOtp = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const normalizedEmail = email.trim();

    if (!normalizedEmail) {
      setError("Email is required.");
      return;
    }

    if (
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
        normalizedEmail
      )
    ) {
      setError("Please enter a valid email address.");
      return;
    }

    try {
      setLoading(true);

      await authService.forgotPassword({
        email: normalizedEmail,
      });

      setEmail(normalizedEmail);
      setOtp("");
      setResetToken("");
      setStep("otp");

      setSuccess(
        "A verification OTP has been sent to your Gmail."
      );
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const normalizedEmail = email.trim();

    if (!normalizedEmail) {
      setError("Email is required.");
      setStep("email");
      return;
    }

    if (otp.length !== 6) {
      setError("Please enter the 6-digit OTP.");
      return;
    }

    try {
      setLoading(true);

      const response =
        await authService.verifyResetOtp(
          normalizedEmail,
          otp
        );

      if (!response.resetToken) {
        throw new Error(
          "OTP verification succeeded but no reset token was returned."
        );
      }

      setResetToken(response.resetToken);
      setStep("password");

      setSuccess(
        "OTP verified. You can now create a new password."
      );
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!resetToken) {
      setError(
        "Your password reset session is invalid. Please request a new OTP."
      );
      setStep("email");
      return;
    }

    const formData = new FormData(
      event.currentTarget
    );

    const newPassword =
      String(
        formData.get("newPassword") ?? ""
      );

    const confirmPassword =
      String(
        formData.get("confirmPassword") ?? ""
      );

    if (newPassword.length < 8) {
      setError(
        "Password must be at least 8 characters long."
      );
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      setLoading(true);

      await authService.resetPassword({
        token: resetToken,
        newPassword,
      });

      setSuccess(
        "Password changed successfully. Redirecting to sign in..."
      );

      /*
       * Give the user a moment to see the success message,
       * then send them to the login page.
       */
      setTimeout(() => {
        navigate("/login", {
          replace: true,
          state: {
            email: email.trim(),
          },
        });
      }, 1000);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6">
      <div className="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-lg flex-col items-center justify-center">

        {/* Brand */}
        <Link
          to="/"
          className="mb-6 flex items-center gap-3 text-slate-900 transition hover:opacity-80"
        >
          <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-sm font-extrabold text-white shadow-sm">
            SS
          </span>

          <span className="text-lg font-bold tracking-tight">
            SkillSwap Campus
          </span>
        </Link>

        {/* Card */}
        <section className="w-full rounded-2xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8">

          {/* EMAIL STEP */}
          {step === "email" && (
            <>
              <div className="mb-7">
                <span className="text-xs font-bold uppercase tracking-wider text-indigo-600">
                  Account recovery
                </span>

                <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                  Reset your password
                </h1>

                <p className="mt-3 text-sm leading-6 text-slate-600">
                  Enter your Gmail address and we&apos;ll
                  send you a 6-digit verification OTP.
                </p>
              </div>

              <form
                className="flex flex-col gap-5"
                onSubmit={handleSendOtp}
                noValidate
              >
                <div className="flex flex-col gap-2">
                  <label
                    htmlFor="forgot-password-email"
                    className="text-sm font-semibold text-slate-700"
                  >
                    Gmail address
                  </label>

                  <input
                    id="forgot-password-email"
                    name="email"
                    type="email"
                    value={email}
                    onChange={handleEmailChange}
                    placeholder="you@gmail.com"
                    autoComplete="email"
                    disabled={loading}
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                  />
                </div>

                {error && (
                  <div
                    className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium leading-5 text-red-700"
                    role="alert"
                  >
                    {error}
                  </div>
                )}

                {success && (
                  <div
                    className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium leading-5 text-emerald-700"
                    role="status"
                  >
                    {success}
                  </div>
                )}

                <button
                  type="submit"
                  disabled={loading}
                  className="inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {loading
                    ? "Sending OTP..."
                    : "Send OTP"}
                </button>
              </form>
            </>
          )}

          {/* OTP STEP */}
          {step === "otp" && (
            <>
              <div className="mb-7">
                <span className="text-xs font-bold uppercase tracking-wider text-indigo-600">
                  Verification
                </span>

                <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                  Verify your OTP
                </h1>

                <p className="mt-3 text-sm leading-6 text-slate-600">
                  We sent a 6-digit OTP to{" "}
                  <span className="font-semibold text-slate-900">
                    {email}
                  </span>
                  .
                </p>
              </div>

              <form
                className="flex flex-col gap-5"
                onSubmit={handleVerifyOtp}
                noValidate
              >
                <div className="flex flex-col gap-2">
                  <label
                    htmlFor="forgot-password-otp"
                    className="text-sm font-semibold text-slate-700"
                  >
                    Verification OTP
                  </label>

                  <input
                    id="forgot-password-otp"
                    name="otp"
                    type="text"
                    inputMode="numeric"
                    autoComplete="one-time-code"
                    value={otp}
                    onChange={handleOtpChange}
                    placeholder="Enter 6-digit OTP"
                    maxLength={6}
                    disabled={loading}
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-center text-lg font-semibold tracking-[0.35em] text-slate-900 outline-none transition placeholder:text-sm placeholder:tracking-normal placeholder:text-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                  />
                </div>

                {error && (
                  <div
                    className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium leading-5 text-red-700"
                    role="alert"
                  >
                    {error}
                  </div>
                )}

                {success && (
                  <div
                    className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium leading-5 text-emerald-700"
                    role="status"
                  >
                    {success}
                  </div>
                )}

                <button
                  type="submit"
                  disabled={
                    loading ||
                    otp.length !== 6
                  }
                  className="inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {loading
                    ? "Verifying..."
                    : "Verify OTP"}
                </button>
              </form>
            </>
          )}

          {/* PASSWORD STEP */}
          {step === "password" && (
            <>
              <div className="mb-7">
                <span className="text-xs font-bold uppercase tracking-wider text-indigo-600">
                  New password
                </span>

                <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                  Create a new password
                </h1>

                <p className="mt-3 text-sm leading-6 text-slate-600">
                  Your OTP has been verified. Create a
                  new password for your account.
                </p>
              </div>

              <form
                className="flex flex-col gap-5"
                onSubmit={handlePasswordSubmit}
                noValidate
              >
                <div className="flex flex-col gap-2">
                  <label
                    htmlFor="new-password"
                    className="text-sm font-semibold text-slate-700"
                  >
                    New password
                  </label>

                  <input
                    id="new-password"
                    name="newPassword"
                    type="password"
                    placeholder="Enter new password"
                    autoComplete="new-password"
                    disabled={loading}
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                  />
                </div>

                <div className="flex flex-col gap-2">
                  <label
                    htmlFor="confirm-password"
                    className="text-sm font-semibold text-slate-700"
                  >
                    Confirm password
                  </label>

                  <input
                    id="confirm-password"
                    name="confirmPassword"
                    type="password"
                    placeholder="Confirm new password"
                    autoComplete="new-password"
                    disabled={loading}
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                  />
                </div>

                {error && (
                  <div
                    className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium leading-5 text-red-700"
                    role="alert"
                  >
                    {error}
                  </div>
                )}

                {success && (
                  <div
                    className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium leading-5 text-emerald-700"
                    role="status"
                  >
                    {success}
                  </div>
                )}

                <button
                  type="submit"
                  disabled={loading || !resetToken}
                  className="inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {loading
                    ? "Changing Password..."
                    : "Change Password"}
                </button>
              </form>
            </>
          )}

          <p className="mt-6 text-center text-sm text-slate-600">
            <Link
              to="/login"
              className="font-semibold text-indigo-600 transition hover:text-indigo-700 hover:underline"
            >
              ← Back to sign in
            </Link>
          </p>
        </section>
      </div>
    </main>
  );
};

export default ForgotPasswordPage;

