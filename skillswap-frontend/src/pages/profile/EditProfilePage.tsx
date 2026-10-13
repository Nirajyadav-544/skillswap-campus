import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import AppShell from "../../components/layout/AppShell";
import ProfileEditForm from "../../components/profile/ProfileEditForm";
import { useAuth } from "../../hooks/useAuth";
import { useProfile } from "../../hooks/useProfile";
import type { UpdateProfileRequest } from "../../types/profile/profile.types";

const EditProfilePage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const {
    profile,
    loading,
    error,
    loadProfile,
    createProfile,
    updateProfile,
    checkProfileExists,
  } = useProfile();

  const [profileExists, setProfileExists] = useState<boolean | null>(null);
  const [checkingProfile, setCheckingProfile] = useState(true);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    let cancelled = false;

    const initialize = async () => {
      setCheckingProfile(true);
      setSaveError(null);

      try {
        const exists = await checkProfileExists();

        if (cancelled) return;

        setProfileExists(exists);

        if (exists) {
          await loadProfile();
        }
      } catch (err) {
        if (!cancelled) {
          setSaveError(
            err instanceof Error
              ? err.message
              : "Unable to load your profile.",
          );
        }
      } finally {
        if (!cancelled) {
          setCheckingProfile(false);
        }
      }
    };

    void initialize();

    return () => {
      cancelled = true;
    };
  }, [checkProfileExists, loadProfile]);

  const handleSubmit = async (data: UpdateProfileRequest) => {
    setSaving(true);
    setSaveError(null);
    setSaved(false);

    try {
      if (profileExists === false) {
        await createProfile(data);
        setProfileExists(true);
      } else {
        await updateProfile(data);
      }

      setSaved(true);

      window.setTimeout(() => {
        navigate("/profile");
      }, 700);
    } catch (err) {
      setSaveError(
        err instanceof Error
          ? err.message
          : "Unable to save your profile.",
      );
    } finally {
      setSaving(false);
    }
  };

  if (checkingProfile || (loading && !profile)) {
    return (
      <AppShell>
        <div className="mx-auto max-w-5xl px-4 py-12">
          <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <div className="mx-auto h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />
            <p className="mt-4 text-sm text-slate-600">
              Loading your profile...
            </p>
          </div>
        </div>
      </AppShell>
    );
  }

  return (
    <AppShell>
      <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">

        <div className="mb-8">
          <Link
            to="/profile"
            className="text-sm font-medium text-slate-700 hover:text-indigo-600"
          >
            ← Back to Profile
          </Link>

          <p className="mt-8 text-sm font-semibold text-indigo-600">
            {profileExists === false
              ? "Profile Setup"
              : "Profile Settings"}
          </p>

          <h1 className="mt-1 text-4xl font-bold text-slate-950">
            {profileExists === false
              ? "Create Your Profile"
              : "Edit Profile"}
          </h1>

          <p className="mt-2 text-base text-slate-500">
            Keep your campus and professional information up to date.
          </p>
        </div>

        <section className="mb-8 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8">
          <div className="flex items-start gap-5">

            <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-xl font-bold text-indigo-600">
              {user?.firstName?.charAt(0)?.toUpperCase() || "U"}
            </div>

            <div className="flex-1">
              <h2 className="text-2xl font-bold text-slate-950">
                Account Information
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                These details come from your account.
              </p>

              <div className="mt-7 grid gap-6 sm:grid-cols-2">

                <div>
                  <p className="text-xs font-semibold uppercase text-slate-400">
                    First Name
                  </p>
                  <p className="mt-1 font-semibold text-slate-900">
                    {user?.firstName || "—"}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-semibold uppercase text-slate-400">
                    Last Name
                  </p>
                  <p className="mt-1 font-semibold text-slate-900">
                    {user?.lastName || "—"}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-semibold uppercase text-slate-400">
                    Email / Gmail
                  </p>
                  <p className="mt-1 break-all font-semibold text-slate-900">
                    {user?.email || "—"}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-semibold uppercase text-slate-400">
                    Verification
                  </p>

                  <div className="mt-2 inline-flex items-center gap-2 rounded-full bg-emerald-50 px-3 py-1 text-sm font-semibold text-emerald-700">
                    <span className="h-2 w-2 rounded-full bg-emerald-500" />
                    {user?.emailVerified
                      ? "Email Verified"
                      : "Email Not Verified"}
                  </div>
                </div>

                <div>
                  <p className="text-xs font-semibold uppercase text-slate-400">
                    Account Role
                  </p>

                  <p className="mt-1 font-semibold uppercase text-slate-900">
                    {user?.role || "STUDENT"}
                  </p>
                </div>

              </div>
            </div>
          </div>
        </section>

        {error && !saveError && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        {saveError && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {saveError}
          </div>
        )}

        {saved && (
          <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">
            Profile saved successfully. Redirecting...
          </div>
        )}

        <ProfileEditForm
          profile={profile}
          loading={saving}
          onSubmit={handleSubmit}
          onCancel={() => navigate("/profile")}
        />

      </div>
    </AppShell>
  );
};

export default EditProfilePage;