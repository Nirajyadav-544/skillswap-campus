
import { type FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";

import authService from "../../services/auth/authService";
import type { RegisterRequest } from "../../types/auth/auth.types";
import { getApiErrorMessage } from "../../services/api/apiError";

const RegisterForm = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState<RegisterRequest>({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    verificationMethod: "EMAIL",
  });

  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!form.firstName.trim()) {
      setError("First name is required.");
      return;
    }

    if (!form.lastName.trim()) {
      setError("Last name is required.");
      return;
    }

    if (!form.email.trim()) {
      setError("Email is required.");
      return;
    }

    if (!form.password) {
      setError("Password is required.");
      return;
    }

    if (form.password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      setLoading(true);

      const response = await authService.register({
        email: form.email.trim(),
        password: form.password,
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        phoneNumber: form.phoneNumber?.trim() || undefined,
        verificationMethod: "EMAIL",
      });

      setSuccess(
        response.message ||
          "Registration successful. Please verify your email."
      );

      navigate("/verify-otp", {
        replace: true,
        state: {
          email: form.email.trim(),
        },
      });
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const inputClassName =
    "w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500";

  const labelClassName =
    "text-sm font-semibold text-slate-700";

  return (
    <form
      onSubmit={handleSubmit}
      noValidate
      className="flex flex-col gap-5"
    >
      {/* Name Fields */}
      <div className="grid gap-5 sm:grid-cols-2">
        <div className="flex flex-col gap-2">
          <label
            htmlFor="register-first-name"
            className={labelClassName}
          >
            First Name
          </label>

          <input
            id="register-first-name"
            name="firstName"
            type="text"
            value={form.firstName}
            onChange={handleChange}
            placeholder="Enter your first name"
            autoComplete="given-name"
            disabled={loading}
            className={inputClassName}
          />
        </div>

        <div className="flex flex-col gap-2">
          <label
            htmlFor="register-last-name"
            className={labelClassName}
          >
            Last Name
          </label>

          <input
            id="register-last-name"
            name="lastName"
            type="text"
            value={form.lastName}
            onChange={handleChange}
            placeholder="Enter your last name"
            autoComplete="family-name"
            disabled={loading}
            className={inputClassName}
          />
        </div>
      </div>

      {/* Email */}
      <div className="flex flex-col gap-2">
        <label
          htmlFor="register-email"
          className={labelClassName}
        >
          Email
        </label>

        <input
          id="register-email"
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          placeholder="Enter your email"
          autoComplete="email"
          disabled={loading}
          className={inputClassName}
        />

        <p className="text-xs text-slate-500">
          We'll send a verification code to this email.
        </p>
      </div>

      {/* Password */}
      <div className="flex flex-col gap-2">
        <label
          htmlFor="register-password"
          className={labelClassName}
        >
          Password
        </label>

        <input
          id="register-password"
          name="password"
          type="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Create a password"
          autoComplete="new-password"
          disabled={loading}
          className={inputClassName}
        />

        <p className="text-xs text-slate-500">
          Password must be at least 8 characters long.
        </p>
      </div>

      {/* Confirm Password */}
      <div className="flex flex-col gap-2">
        <label
          htmlFor="register-confirm-password"
          className={labelClassName}
        >
          Confirm Password
        </label>

        <input
          id="register-confirm-password"
          type="password"
          value={confirmPassword}
          onChange={(event) =>
            setConfirmPassword(event.target.value)
          }
          placeholder="Confirm your password"
          autoComplete="new-password"
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
        type="submit"
        disabled={loading}
        className="mt-1 inline-flex min-h-12 w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {loading
          ? "Creating account..."
          : "Create Account"}
      </button>
    </form>
  );
};

export default RegisterForm;

