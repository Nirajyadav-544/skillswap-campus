import { useEffect, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import authService from "../../services/auth/authService";
import { getApiErrorMessage } from "../../services/api/apiError";

import "../../styles/auth.css";

interface LocationState {
  email?: string;
}

const OTP_LENGTH = 6;
const RESEND_COOLDOWN_SECONDS = 60;

const VerifyOtpPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const state = location.state as LocationState | null;

  const [email, setEmail] = useState(state?.email ?? "");
  const [otp, setOtp] = useState("");

  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [cooldown, setCooldown] = useState(0);

  useEffect(() => {
    if (cooldown <= 0) {
      return;
    }

    const timer = window.setInterval(() => {
      setCooldown((previous) => {
        if (previous <= 1) {
          window.clearInterval(timer);
          return 0;
        }

        return previous - 1;
      });
    }, 1000);

    return () => {
      window.clearInterval(timer);
    };
  }, [cooldown]);

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
      .slice(0, OTP_LENGTH);

    setOtp(value);
    setError("");
    setSuccess("");
  };

  const handleSubmit = async (
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

    if (otp.length !== OTP_LENGTH) {
      setError("Please enter the 6-digit verification code.");
      return;
    }

    try {
      setLoading(true);

      await authService.verifyOtp(
        normalizedEmail,
        {
          otp,
          method: "EMAIL",
        }
      );

      setOtp("");

      navigate("/login", {
        replace: true,
        state: {
          email: normalizedEmail,
          verified: true,
        },
      });
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    setError("");
    setSuccess("");

    const normalizedEmail = email.trim();

    if (!normalizedEmail) {
      setError("Email is required.");
      return;
    }

    if (cooldown > 0) {
      return;
    }

    try {
      setResending(true);

      const response = await authService.resendOtp(
        normalizedEmail,
        {
          method: "EMAIL",
        }
      );

      setSuccess(
        response.message ||
          "A new verification code has been sent to your email."
      );

      setCooldown(RESEND_COOLDOWN_SECONDS);
      setOtp("");
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setResending(false);
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-container">

        <Link
          to="/"
          className="auth-brand"
        >
          <span className="auth-brand-mark">
            SS
          </span>

          <span className="auth-brand-name">
            SkillSwap Campus
          </span>
        </Link>

        <section className="auth-card">

          <div className="auth-heading">
            <span className="auth-eyebrow">
              Email verification
            </span>

            <h1>
              Verify your email
            </h1>

            <p>
              Enter the 6-digit verification code
              sent to your email address.
            </p>
          </div>

          <form
            className="auth-form"
            onSubmit={handleSubmit}
            noValidate
          >

            <div className="auth-field">
              <label htmlFor="verify-email">
                Email address
              </label>

              <input
                id="verify-email"
                name="email"
                type="email"
                value={email}
                onChange={handleEmailChange}
                placeholder="you@example.com"
                autoComplete="email"
                disabled={loading || resending}
              />
            </div>

            <div className="auth-field">
              <label htmlFor="verify-otp">
                Verification code
              </label>

              <input
                id="verify-otp"
                name="otp"
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                maxLength={OTP_LENGTH}
                value={otp}
                onChange={handleOtpChange}
                placeholder="000000"
                autoComplete="one-time-code"
                disabled={loading || resending}
              />
            </div>

            {error && (
              <div
                className="auth-message auth-message-error"
                role="alert"
              >
                {error}
              </div>
            )}

            {success && (
              <div
                className="auth-message auth-message-success"
                role="status"
              >
                {success}
              </div>
            )}

            <button
              className="auth-submit-button"
              type="submit"
              disabled={
                loading ||
                resending ||
                otp.length !== OTP_LENGTH
              }
            >
              {loading
                ? "Verifying..."
                : "Verify email"}
            </button>

            <button
              type="button"
              className="auth-submit-button"
              onClick={handleResend}
              disabled={
                loading ||
                resending ||
                cooldown > 0
              }
            >
              {resending
                ? "Sending..."
                : cooldown > 0
                  ? `Resend code in ${cooldown}s`
                  : "Resend verification code"}
            </button>

          </form>

          <p className="auth-switch">
            Already verified?{" "}
            <Link to="/login">
              Sign in
            </Link>
          </p>

        </section>

        <Link
          to="/"
          className="auth-home-link"
        >
          ← Back to SkillSwap Campus
        </Link>

      </div>
    </main>
  );
};

export default VerifyOtpPage;