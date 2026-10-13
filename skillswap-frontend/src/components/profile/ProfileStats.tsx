import type { ProfileStats as ProfileStatsData } from "../../types/profile/profile.types";

interface ProfileStatsProps {
  stats?: ProfileStatsData;
  loading?: boolean;
}

const ProfileStats = ({
  stats,
  loading = false,
}: ProfileStatsProps) => {
  const items = [
    {
      label: "Skills",
      value: stats?.skillsCount,
      icon: "✦",
      description: "Skills in your profile",
    },
    {
      label: "Exchanges",
      value: stats?.exchangesCount,
      icon: "⇄",
      description: "Skill exchanges",
    },
    {
      label: "Projects",
      value: stats?.projectsCount,
      icon: "◈",
      description: "Projects joined",
    },
    {
      label: "Teams",
      value: stats?.teamsCount,
      icon: "♧",
      description: "Teams joined",
    },
  ];

  if (loading) {
    return (
      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {items.map((item) => (
          <div
            key={item.label}
            className="animate-pulse rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
          >
            <div className="h-11 w-11 rounded-xl bg-slate-200" />
            <div className="mt-5 h-8 w-16 rounded bg-slate-200" />
            <div className="mt-2 h-4 w-24 rounded bg-slate-100" />
          </div>
        ))}
      </section>
    );
  }

  return (
    <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {items.map((item) => (
        <article
          key={item.label}
          className="group rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-indigo-200 hover:shadow-lg"
        >
          <div className="flex items-start justify-between">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-50 text-lg font-bold text-indigo-600">
              {item.icon}
            </div>

            <span className="text-xs font-medium text-slate-400">
              Campus
            </span>
          </div>

          <p className="mt-5 text-3xl font-bold tracking-tight text-slate-900">
            {item.value ?? "—"}
          </p>

          <h3 className="mt-1 text-sm font-bold text-slate-700">
            {item.label}
          </h3>

          <p className="mt-1 text-xs leading-5 text-slate-400">
            {item.description}
          </p>
        </article>
      ))}
    </section>
  );
};

export default ProfileStats;
