
import {
  Component,
  type ErrorInfo,
  type ReactNode,
} from "react";

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

class ErrorBoundary extends Component<
  ErrorBoundaryProps,
  ErrorBoundaryState
> {
  public state: ErrorBoundaryState = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(
    error: Error
  ): ErrorBoundaryState {
    return {
      hasError: true,
      error,
    };
  }

  public componentDidCatch(
    error: Error,
    errorInfo: ErrorInfo
  ): void {
    console.error(
      "SkillSwap Campus application error:",
      error
    );

    console.error(
      "React component error information:",
      errorInfo
    );
  }

  private handleReload = (): void => {
    window.location.reload();
  };

  private handleGoHome = (): void => {
    window.location.href = "/";
  };

  public render(): ReactNode {
    if (!this.state.hasError) {
      return this.props.children;
    }

    const errorMessage =
      this.state.error?.message ||
      "An unexpected application error occurred.";

    return (
      <main className="flex min-h-screen items-center justify-center bg-slate-50 px-6 py-8">
        <section className="w-full max-w-[620px] rounded-[20px] border border-slate-200 bg-white p-6 shadow-[0_12px_30px_rgba(15,23,42,0.10)] sm:p-10">
          {/* Error Icon */}
          <div className="mb-5 grid h-14 w-14 place-items-center rounded-full bg-red-50 text-2xl font-extrabold text-red-600">
            !
          </div>

          {/* Heading */}
          <h1 className="mb-3 text-[28px] font-bold leading-tight text-slate-800">
            Something went wrong
          </h1>

          {/* Description */}
          <p className="mb-6 leading-7 text-slate-500">
            SkillSwap Campus encountered an unexpected
            application error. The page did not load
            correctly.
          </p>

          {/* Error Details */}
          <div className="mb-6 overflow-x-auto rounded-xl border border-slate-200 bg-slate-50 p-4">
            <strong className="mb-2 block text-sm font-bold text-slate-800">
              Error details
            </strong>

            <code className="block break-words text-[13px] leading-6 text-red-600">
              {errorMessage}
            </code>
          </div>

          {/* Actions */}
          <div className="flex flex-wrap gap-3">
            <button
              type="button"
              onClick={this.handleReload}
              className="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              Reload Application
            </button>

            <button
              type="button"
              onClick={this.handleGoHome}
              className="rounded-xl border border-slate-300 bg-white px-5 py-3 text-sm font-semibold text-slate-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              Go to Home
            </button>
          </div>
        </section>
      </main>
    );
  }
}

export default ErrorBoundary;

