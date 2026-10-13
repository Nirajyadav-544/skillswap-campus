import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";

import AppShell from "../../components/layout/AppShell";
import ProfileHeader from "../../components/profile/ProfileHeader";
import ProfileOverview from "../../components/profile/ProfileOverview";
import ProfileStats from "../../components/profile/ProfileStats";
import ProfileSkills from "../../components/profile/ProfileSkills";
import { useProfile } from "../../hooks/useProfile";

const ProfilePage = () => {
  const navigate = useNavigate();

  const {
    profile,
    loading,
    error,
    loadProfile,
  } = useProfile();

  /*
   * If the authenticated user does not have a student profile yet,
   * send them to the existing Profile Setup page.
   *
   * EditProfilePage already handles:
   *   GET /v1/profile/me/exists
   *   POST /v1/profile
   *   PUT /v1/profile/me
   */
  useEffect(() => {
    if (
      !loading &&
      !profile &&
      error &&
      error.toLowerCase().includes("student profile not found")
    ) {
      navigate("/profile/edit", {
        replace: true,
      });
    }
  }, [loading, profile, error, navigate]);

  if (loading && !profile) {
    return (
      <AppShell>
        <div className="flex min-h-[60vh] items-center justify-center px-4">
          <div className="text-center">
            <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

            <p className="text-sm font-medium text-slate-600">
              Loading your profile...
            </p>
          </div>
        </div>
      </AppShell>
    );
  }

  /*
   * If the profile is missing, the useEffect above redirects
   * the user to /profile/edit.
   *
   * Keep a small fallback here in case navigation has not happened yet.
   */
  if (
    !profile &&
    error &&
    error.toLowerCase().includes("student profile not found")
  ) {
    return (
      <AppShell>
        <div className="flex min-h-[60vh] items-center justify-center px-4">
          <div className="text-center">
            <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

            <p className="text-sm font-medium text-slate-600">
              Opening profile setup...
            </p>
          </div>
        </div>
      </AppShell>
    );
  }

  /*
   * Other profile-loading errors should still be shown normally.
   */
  if (error && !profile) {
    return (
      <AppShell>
        <div className="mx-auto flex min-h-[60vh] max-w-2xl items-center justify-center px-4">
          <div className="w-full rounded-2xl border border-red-200 bg-red-50 p-6 text-center shadow-sm">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-red-100 text-xl font-bold text-red-700">
              !
            </div>

            <h1 className="text-xl font-bold text-red-900">
              Unable to load your profile
            </h1>

            <p className="mt-2 text-sm leading-6 text-red-700">
              {error}
            </p>

            <button
              type="button"
              onClick={() => void loadProfile()}
              className="mt-5 inline-flex items-center justify-center rounded-xl bg-red-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
            >
              Try Again
            </button>
          </div>
        </div>
      </AppShell>
    );
  }

  if (!profile) {
    return (
      <AppShell>
        <div className="flex min-h-[60vh] items-center justify-center px-4">
          <div className="text-center">
            <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

            <p className="text-sm font-medium text-slate-600">
              Preparing your profile...
            </p>
          </div>
        </div>
      </AppShell>
    );
  }

  return (
    <AppShell>
      <div className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 lg:px-8 lg:py-8">

        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
              My Profile
            </p>

            <h1 className="mt-1 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
              Your SkillSwap Profile
            </h1>

            <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
              Manage your personal information, skills, and professional
              profile from one place.
            </p>
          </div>

          <Link
            to="/profile/edit"
            className="inline-flex w-fit items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
          >
            <svg
              className="h-4 w-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 20h9"
              />

              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"
              />
            </svg>

            Edit Profile
          </Link>
        </div>

        {loading && profile && (
          <div className="mb-5 flex items-center gap-3 rounded-xl border border-indigo-100 bg-indigo-50 px-4 py-3">
            <div className="h-4 w-4 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />

            <p className="text-sm font-medium text-indigo-700">
              Updating your profile...
            </p>
          </div>
        )}

        {error && profile && (
          <div className="mb-5 flex flex-col gap-3 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm font-semibold text-amber-900">
                Profile update warning
              </p>

              <p className="mt-1 text-sm text-amber-800">
                {error}
              </p>
            </div>

            <button
              type="button"
              onClick={() => void loadProfile()}
              className="w-fit shrink-0 rounded-lg bg-amber-100 px-3 py-1.5 text-xs font-semibold text-amber-900 transition hover:bg-amber-200"
            >
              Retry
            </button>
          </div>
        )}

        <div className="space-y-6">

          <section>
            <ProfileHeader profile={profile} />
          </section>

          <section>
            <ProfileStats />
          </section>

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">

            <section className="lg:col-span-2">
              <ProfileOverview profile={profile} />
            </section>

            <section className="lg:col-span-1">
              <ProfileSkills />
            </section>

          </div>

          <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
            <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">

              <div>
                <h2 className="text-lg font-bold text-slate-900">
                  Keep your profile updated
                </h2>

                <p className="mt-1 max-w-2xl text-sm leading-6 text-slate-500">
                  Your profile helps other students understand your skills,
                  interests, and experience. Update it whenever your skills
                  or information change.
                </p>
              </div>

              <Link
                to="/profile/edit"
                className="inline-flex w-fit shrink-0 items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 shadow-sm transition hover:border-indigo-200 hover:bg-indigo-50 hover:text-indigo-700"
              >
                Edit My Profile

                <svg
                  className="h-4 w-4"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M5 12h14"
                  />

                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="m13 6 6 6-6 6"
                  />
                </svg>
              </Link>

            </div>
          </section>

        </div>
      </div>
    </AppShell>
  );
};

export default ProfilePage;