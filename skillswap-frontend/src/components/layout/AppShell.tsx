import type { ReactNode } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import { APP_NAME } from "../../utils/constants";

interface AppShellProps {
  children: ReactNode;
}

const AppShell = ({ children }: AppShellProps) => {
  const navigate = useNavigate();
  const location = useLocation();

  const {
    user,
    isAuthenticated,
    logout,
  } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const getInitials = () => {
    if (!user) {
      return "?";
    }

    const first = user.firstName?.trim().charAt(0) ?? "";
    const last = user.lastName?.trim().charAt(0) ?? "";

    const initials = `${first}${last}`.toUpperCase();

    return initials || user.email.charAt(0).toUpperCase();
  };

  const isActive = (path: string) =>
    location.pathname === path ||
    location.pathname.startsWith(`${path}/`);

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <header className="sticky top-0 z-50 border-b border-slate-200 bg-white/95 shadow-sm backdrop-blur">
        <div className="mx-auto flex min-h-[72px] w-full max-w-7xl items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">

          {/* Brand */}
          <Link
            to="/"
            className="flex shrink-0 items-center gap-3 rounded-xl outline-none transition hover:opacity-90 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            aria-label={`${APP_NAME} home`}
          >
            <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 text-sm font-black tracking-tight text-white shadow-md shadow-indigo-200">
              SS
            </span>

            <span className="hidden text-base font-extrabold tracking-tight text-slate-900 sm:block">
              SkillSwap Campus
            </span>

            <span className="text-base font-extrabold tracking-tight text-slate-900 sm:hidden">
              SSC
            </span>
          </Link>

          {isAuthenticated && user ? (
            <nav
              className="flex items-center gap-1 sm:gap-2"
              aria-label="Main navigation"
            >

              {/* Dashboard */}
              <Link
                to="/dashboard"
                className={`rounded-xl px-3 py-2 text-sm font-semibold transition sm:px-4 ${
                  isActive("/dashboard")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                Dashboard
              </Link>

              {/* Students */}
              <Link
                to="/students"
                className={`rounded-xl px-3 py-2 text-sm font-semibold transition sm:px-4 ${
                  isActive("/students")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                <span className="hidden sm:inline">
                  Students
                </span>

                <span className="sm:hidden">
                  👥
                </span>
              </Link>

              {/* Profile */}
              <Link
                to="/profile"
                className={`hidden rounded-xl px-3 py-2 text-sm font-semibold transition md:block md:px-4 ${
                  isActive("/profile")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                Profile
              </Link>

              {/* Skills */}
              <Link
                to="/skills"
                className={`hidden rounded-xl px-3 py-2 text-sm font-semibold transition lg:block lg:px-4 ${
                  isActive("/skills")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                Skills
              </Link>

              {/* My Skills */}
              <Link
                to="/skills/my"
                className={`hidden rounded-xl px-3 py-2 text-sm font-semibold transition lg:block lg:px-4 ${
                  isActive("/skills/my")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                My Skills
              </Link>

              {/* Skill DNA */}
              <Link
                to="/skill-dna"
                className={`rounded-xl px-3 py-2 text-sm font-semibold transition sm:px-4 ${
                  isActive("/skill-dna")
                    ? "bg-indigo-50 text-indigo-700"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                🧬{" "}
                <span className="hidden sm:inline">
                  Skill DNA
                </span>

                <span className="sm:hidden">
                  DNA
                </span>
              </Link>

              {/* User / Logout */}
              <div className="ml-1 flex items-center gap-2 border-l border-slate-200 pl-2 sm:ml-2 sm:pl-3">

                <div className="grid h-9 w-9 shrink-0 place-items-center rounded-full bg-gradient-to-br from-indigo-500 to-violet-600 text-xs font-bold text-white ring-2 ring-white shadow-sm">
                  {getInitials()}
                </div>

                <div className="hidden max-w-[130px] lg:block">
                  <p className="truncate text-sm font-semibold text-slate-900">
                    {user.firstName || "Student"}
                  </p>

                  <p className="truncate text-xs text-slate-500">
                    {user.email}
                  </p>
                </div>

                <button
                  type="button"
                  onClick={handleLogout}
                  className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-600 transition hover:border-red-200 hover:bg-red-50 hover:text-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
                >
                  <span className="hidden sm:inline">
                    Logout
                  </span>

                  <span className="sm:hidden">
                    ↪
                  </span>
                </button>
              </div>
            </nav>
          ) : (
            <nav
              className="flex items-center gap-2"
              aria-label="Authentication navigation"
            >
              <Link
                to="/login"
                className="rounded-xl px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 hover:text-slate-900 sm:px-4"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              >
                Register
              </Link>
            </nav>
          )}
        </div>
      </header>

      <main className="min-h-0 flex-1">
        {children}
      </main>

      <footer className="border-t border-slate-200 bg-white">
        <div className="mx-auto flex min-h-[64px] w-full max-w-7xl items-center justify-between gap-4 px-4 text-xs text-slate-500 sm:px-6 lg:px-8">
          <span>
            © {new Date().getFullYear()} {APP_NAME}
          </span>

          <span className="hidden sm:inline">
            Campus Skill & Career Intelligence
          </span>
        </div>
      </footer>
    </div>
  );
};

export default AppShell;