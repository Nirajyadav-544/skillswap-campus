
import { useState } from "react";
import type {
  ChangeEvent,
  FormEvent,
} from "react";
import {
  Link,
  useNavigate,
} from "react-router-dom";

import authService from "../../services/auth/authService";
import { useAuth } from "../../hooks/useAuth";
import type {
  LoginRequest,
} from "../../types/auth/auth.types";
import {
  getApiErrorMessage,
} from "../../services/api/apiError";

const LoginPage = () => {
  const navigate = useNavigate();

  const { setAuth } = useAuth();

  const [form, setForm] =
    useState<LoginRequest>({
      email: "",
      password: "",
    });

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const handleChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    const {
      name,
      value,
    } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setError("");
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

      const response =
        await authService.login({
          email,
          password: form.password,
        });

      if (!response.accessToken) {
        throw new Error(
          "Login succeeded but no access token was returned."
        );
      }

      setAuth(
        response.accessToken,
        {
          id: response.userId,
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          role: response.role,
          accountVerified: response.verified,
          emailVerified: response.verified,
        }
      );

      setSuccess(
        "Login successful."
      );

      navigate(
        "/dashboard",
        {
          replace: true,
        }
      );
    } catch (err) {
  const message = getApiErrorMessage(err);

  if (
    message.toLowerCase().includes(
      "please verify your account"
    )
  ) {
    navigate("/verify-otp", {
      replace: true,
      state: {
        email,
        fromLogin: true,
      },
    });

    return;
  }

  setError(message);
}
    
    finally {
      setLoading(false);
    }
  };

  const inputClassName =
    "w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-300 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-50 disabled:text-slate-500";

  const labelClassName =
    "text-sm font-semibold text-slate-700";

  return (
    <main className="relative flex min-h-screen items-center justify-center overflow-hidden bg-gradient-to-br from-indigo-50 via-white to-violet-50 px-4 py-8 sm:px-6">

      {/* Decorative background */}
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-32 -top-32 h-80 w-80 rounded-full bg-indigo-200/40 blur-3xl"
      />

      <div
        aria-hidden="true"
        className="pointer-events-none absolute -bottom-32 -left-32 h-80 w-80 rounded-full bg-violet-200/40 blur-3xl"
      />

      <div className="relative z-10 flex w-full max-w-md flex-col items-center">

        {/* Brand */}
        <Link
          to="/"
          className="mb-6 inline-flex items-center gap-3 text-slate-900 no-underline transition hover:opacity-80"
        >
          <span className="grid h-11 w-11 place-items-center rounded-xl bg-gradient-to-br from-indigo-500 to-indigo-700 text-sm font-black tracking-tight text-white shadow-lg shadow-indigo-500/25">
            SS
          </span>

          <span className="text-xl font-extrabold tracking-tight">
            SkillSwap Campus
          </span>
        </Link>

        {/* Login Card */}
        <section className="w-full rounded-3xl border border-white/80 bg-white/90 p-6 shadow-2xl shadow-indigo-950/10 backdrop-blur-xl sm:p-9">

          {/* Heading */}
          <div className="mb-7 text-center">
            <span className="inline-flex items-center rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1.5 text-[11px] font-extrabold uppercase tracking-[0.12em] text-indigo-700">
              Welcome back
            </span>

            <h1 className="mt-4 text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">
              Sign in account
            </h1>

            <p className="mx-auto mt-3 max-w-sm text-sm leading-6 text-slate-500">
              Continue building your skills, connecting with students,
              and growing your career profile.
            </p>
          </div>

          {/* Login Form */}
          <form
            className="flex flex-col gap-5"
            onSubmit={handleSubmit}
            noValidate
          >
            {/* Email */}
            <div className="flex flex-col gap-2">
              <label
                htmlFor="login-email"
                className={labelClassName}
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
                className={inputClassName}
              />
            </div>

            {/* Password */}
            <div className="flex flex-col gap-2">
              <div className="flex items-center justify-between gap-4">
                <label
                  htmlFor="login-password"
                  className={labelClassName}
                >
                  Password
                </label>

                <Link
                  to="/forgot-password"
                  className="text-xs font-semibold text-indigo-600 transition hover:text-indigo-700 hover:underline"
                >
                  Forgot password?
                </Link>
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
                className={inputClassName}
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
              className="mt-1 inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 to-indigo-700 px-5 py-3 text-sm font-extrabold text-white shadow-lg shadow-indigo-500/20 transition hover:-translate-y-0.5 hover:from-indigo-700 hover:to-indigo-800 hover:shadow-xl hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 active:translate-y-0 disabled:cursor-not-allowed disabled:opacity-60 disabled:hover:translate-y-0"
              type="submit"
              disabled={loading}
            >
              {loading
                ? "Signing you in..."
                : "Sign in"}
            </button>
          </form>

          {/* Register */}
          <p className="mt-7 text-center text-sm text-slate-500">
            Don't have an account?{" "}
            <Link
              to="/register"
              className="font-bold text-indigo-600 transition hover:text-indigo-700 hover:underline"
            >
              Create an account
            </Link>
          </p>
        </section>

        {/* Home */}
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

export default LoginPage;

