
import { useState } from "react";
import type {
  FormEvent,
  ChangeEvent,
} from "react";
import { Link, useNavigate } from "react-router-dom";

import authService from "../../services/auth/authService";
import { useAuth } from "../../hooks/useAuth";
import type { LoginRequest } from "../../types/auth/auth.types";
import { getApiErrorMessage } from "../../services/api/apiError";

const LoginForm = () => {
  const navigate = useNavigate();
  const { setAuth } = useAuth();

  const [form, setForm] = useState<LoginRequest>({
    email: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setError("");
    setSuccess("");
  };

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const email = form.email.trim();

    if (!email) {
      setError("Email is required.");
      return;
    }

    if (!form.password) {
      setError("Password is required.");
      return;
    }

    try {
      setLoading(true);

      const response = await authService.login({
        email,
        password: form.password,
      });

      const token = response.accessToken;

      if (!token) {
        throw new Error(
          "Login succeeded but no access token was returned."
        );
      }

      setAuth(token, {
        id: response.id,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        accountVerified: response.accountVerified,
      });

      setSuccess("Login successful.");

      navigate("/dashboard", {
        replace: true,
      });
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const hasEmail = form.email.trim().length > 0;

  return (
    <form
      className="flex flex-col gap-5"
      onSubmit={handleSubmit}
      noValidate
    >
      {/* Email */}
      <div className="flex flex-col gap-2">
        <label
          htmlFor="login-email"
          className="text-sm font-semibold text-slate-700"
        >
          Email address
        </label>

        <input
          id="login-email"
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          placeholder="you@example.com"
          autoComplete="email"
          disabled={loading}
          className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500"
        />
      </div>

      {/* Password */}
      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between gap-4">
          <label
            htmlFor="login-password"
            className="text-sm font-semibold text-slate-700"
          >
            Password
          </label>

          {hasEmail ? (
            <Link
              to="/forgot-password"
              state={{
                email: form.email.trim(),
              }}
              className="text-sm font-medium text-indigo-600 transition hover:text-indigo-700 hover:underline"
            >
              Forgot password?
            </Link>
          ) : (
            <span
              className="cursor-not-allowed text-sm font-medium text-slate-400"
              title="Enter your email address first"
            >
              Forgot password?
            </span>
          )}
        </div>

        <input
          id="login-password"
          name="password"
          type="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Enter your password"
          autoComplete="current-password"
          disabled={loading}
          className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500"
        />
      </div>

      {/* Error */}
      {error && (
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
        className="mt-1 inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
        type="submit"
        disabled={loading}
      >
        {loading ? "Signing you in..." : "Sign in"}
      </button>
    </form>
  );
};

export default LoginForm;

