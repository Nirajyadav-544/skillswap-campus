import type { Profile } from "../../types/profile/profile.types";

interface ProfileOverviewProps {
  profile: Profile;
}

const ProfileOverview = ({
  profile,
}: ProfileOverviewProps) => {
  const items = [
    {
      label: "Full Name",
      value:
        `${profile.firstName} ${profile.lastName}`.trim() ||
        "Not provided",
    },
    {
      label: "Email",
      value: profile.email || "Not provided",
    },
    {
      label: "Role",
      value: profile.role || "Not provided",
    },
    {
      label: "College",
      value: profile.college || "Not provided",
    },
    {
      label: "Department",
      value: profile.department || "Not provided",
    },
    {
      label: "Course",
      value: profile.course || "Not provided",
    },
    {
      label: "Year of Study",
      value:
        profile.yearOfStudy !== undefined
          ? String(profile.yearOfStudy)
          : "Not provided",
    },
    {
      label: "Graduation Year",
      value:
        profile.graduationYear !== undefined
          ? String(profile.graduationYear)
          : "Not provided",
    },
    {
      label: "Location",
      value: profile.location || "Not provided",
    },
  ];

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <h2 className="text-lg font-bold text-slate-900">
          Profile Overview
        </h2>

        <p className="mt-1 text-sm text-slate-500">
          Your basic academic and account information.
        </p>
      </div>

      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {items.map((item) => (
          <div
            key={item.label}
            className="rounded-xl bg-slate-50 p-4"
          >
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
              {item.label}
            </p>

            <p className="mt-1 break-words text-sm font-medium text-slate-800">
              {item.value}
            </p>
          </div>
        ))}
      </div>

      {profile.bio && (
        <div className="mt-6 rounded-xl bg-slate-50 p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
            About
          </p>

          <p className="mt-2 whitespace-pre-wrap text-sm leading-6 text-slate-700">
            {profile.bio}
          </p>
        </div>
      )}
    </section>
  );
};

export default ProfileOverview;
