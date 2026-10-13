import type { Profile } from "../../types/profile/profile.types";

interface ProfileHeaderProps {
  profile: Profile;
  onEdit?: () => void;
}

const ProfileHeader = ({
  profile,
  onEdit,
}: ProfileHeaderProps) => {
  const initials =
    `${profile.firstName?.charAt(0) ?? ""}${profile.lastName?.charAt(0) ?? ""}`
      .toUpperCase() || "?";

  const fullName =
    `${profile.firstName} ${profile.lastName}`.trim() ||
    "Your Profile";

  return (
    <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div className="h-32 bg-gradient-to-r from-indigo-600 via-violet-600 to-purple-600" />

      <div className="px-6 pb-6">
        <div className="-mt-12 flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
            <div className="flex h-24 w-24 shrink-0 items-center justify-center overflow-hidden rounded-2xl border-4 border-white bg-indigo-100 text-2xl font-bold text-indigo-700 shadow-md">
              {profile.profileImageUrl ? (
                <img
                  src={profile.profileImageUrl}
                  alt={fullName}
                  className="h-full w-full object-cover"
                />
              ) : (
                initials
              )}
            </div>

            <div className="pb-1">
              <div className="flex flex-wrap items-center gap-2">
                <h1 className="text-2xl font-bold text-slate-900">
                  {fullName}
                </h1>

                {profile.emailVerified && (
                  <span className="rounded-full bg-emerald-100 px-2.5 py-1 text-xs font-semibold text-emerald-700">
                    Email Verified
                  </span>
                )}

                {profile.profileCompleted && (
                  <span className="rounded-full bg-indigo-100 px-2.5 py-1 text-xs font-semibold text-indigo-700">
                    Profile Complete
                  </span>
                )}
              </div>

              {profile.headline && (
                <p className="mt-1 text-sm font-medium text-slate-600">
                  {profile.headline}
                </p>
              )}

              <p className="mt-1 text-sm text-slate-500">
                {profile.email}
              </p>
            </div>
          </div>

          {onEdit && (
            <button
              type="button"
              onClick={onEdit}
              className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
            >
              Edit Profile
            </button>
          )}
        </div>
      </div>
    </section>
  );
};

export default ProfileHeader;
