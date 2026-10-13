
import { useMemo, useState } from "react";
import type { FormEvent, ChangeEvent } from "react";
import {
  Link,
  useNavigate,
  useSearchParams,
} from "react-router-dom";

import authService from "../../services/auth/authService";
import { getApiErrorMessage } from "../../services/api/apiError";

const ResetPasswordPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const token = useMemo(
    () => searchParams.get("token")?.trim() || "",
    [searchParams]
  );

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handlePasswordChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    setPassword(event.target.value);
    setError("");
  };

  const handleConfirmPasswordChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    setConfirmPassword(event.target.value);
    setError("");
  };

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!token) {
      setError(
        "This password reset link is invalid or incomplete."
      );
      return;
    }

    if (!password) {
      setError("Password is required.");
      return;
    }

    if (password.length < 8) {
      setError(
        "Password must be between 8 and 100 characters."
      );
      return;
    }

    if (password.length > 100) {
      setError(
        "Password must be between 8 and 100 characters."
      );
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      setLoading(true);

      const response = await authService.resetPassword({
        token,
        newPassword: password,
      });

      setSuccess(
        response.message ||
          "Password reset successfully. You can now sign in."
      );

      setPassword("");
      setConfirmPassword("");

      window.setTimeout(() => {
        navigate("/login", { replace: true });
      }, 1800);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const inputClassName =
    "w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500";

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
          {/* Heading */}
          <div className="mb-7">
            <span className="text-xs font-bold uppercase tracking-wider text-indigo-600">
              Secure account recovery
            </span>

            <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
              Create a new password
            </h1>

            <p className="mt-3 text-sm leading-6 text-slate-600">
              Choose a strong password for your SkillSwap
              Campus account.
            </p>
          </div>

          {/* Form */}
          <form
            className="flex flex-col gap-5"
            onSubmit={handleSubmit}
            noValidate
          >
            {/* New Password */}
            <div className="flex flex-col gap-2">
              <label
                htmlFor="reset-password"
                className="text-sm font-semibold text-slate-700"
              >
                New password
              </label>

              <input
                id="reset-password"
                type="password"
                value={password}
                onChange={handlePasswordChange}
                placeholder="Create a new password"
                autoComplete="new-password"
                disabled={loading || !token}
                className={inputClassName}
              />

              <small className="text-xs text-slate-500">
                Use between 8 and 100 characters.
              </small>
            </div>

            {/* Confirm Password */}
            <div className="flex flex-col gap-2">
              <label
                htmlFor="reset-confirm-password"
                className="text-sm font-semibold text-slate-700"
              >
                Confirm new password
              </label>

              <input
                id="reset-confirm-password"
                type="password"
                value={confirmPassword}
                onChange={handleConfirmPasswordChange}
                placeholder="Repeat your new password"
                autoComplete="new-password"
                disabled={loading || !token}
                className={inputClassName}
              />
            </div>

            {/* Missing Token */}
            {!token && (
              <div
                className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium leading-5 text-red-700"
                role="alert"
              >
                This password reset link is invalid or
                missing its reset token.
              </div>
            )}

            {/* API / Validation Error */}
            {error && token && (
              <div
                className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium leading-5 text-red-700"
                role="alert"
              >
                {error}
              </div>
            )}

            {/* Success */}
            {success && (
              <div
                className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium leading-5 text-emerald-700"
                role="status"
              >
                {success}
              </div>
            )}

            {/* Submit */}
            <button
              className="inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
              type="submit"
              disabled={loading || !token}
            >
              {loading
                ? "Updating password..."
                : "Update password"}
            </button>
          </form>

          {/* Sign In */}
          <p className="mt-6 text-center text-sm text-slate-600">
            Remember your password?{" "}
            <Link
              to="/login"
              className="font-semibold text-indigo-600 transition hover:text-indigo-700 hover:underline"
            >
              Back to sign in
            </Link>
          </p>
        </section>

        {/* Home Link */}
        <Link
          to="/"
          className="mt-6 text-sm font-medium text-slate-500 transition hover:text-indigo-600"
        >
          ← Back to SkillSwap Campus
        </Link>
      </div>
    </main>
  );
};

export default ResetPasswordPage;

