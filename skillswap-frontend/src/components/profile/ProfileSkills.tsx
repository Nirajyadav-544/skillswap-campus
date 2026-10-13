import type { Skill } from "../../types/skills/skill.types";

interface ProfileSkillsProps {
  skills?: Skill[];
  onViewAll?: () => void;
}

export default function ProfileSkills({
  skills = [],
  onViewAll,
}: ProfileSkillsProps) {
  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-5 flex items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Skills</h2>
          <p className="mt-1 text-sm text-slate-500">
            Skills associated with this profile
          </p>
        </div>

        <div className="flex items-center gap-3">
          <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-700">
            {skills.length}
          </span>

          {onViewAll && skills.length > 0 && (
            <button
              type="button"
              onClick={onViewAll}
              className="rounded-xl border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              View all
            </button>
          )}
        </div>
      </div>

      {skills.length === 0 ? (
        <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
          <p className="font-medium text-slate-700">
            No skills added yet
          </p>

          <p className="mt-1 text-sm text-slate-500">
            Add skills to build your Skill DNA.
          </p>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {skills.map((skill) => (
            <div
              key={skill.id}
              className="rounded-xl border border-slate-200 bg-slate-50 p-4 transition hover:border-slate-300 hover:shadow-sm"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0">
                  <h3 className="truncate font-semibold text-slate-900">
                    {skill.name}
                  </h3>

                  <p className="mt-1 text-xs font-medium uppercase tracking-wide text-slate-500">
                    {skill.category}
                  </p>
                </div>

                {skill.active && (
                  <span className="shrink-0 rounded-full bg-emerald-100 px-2 py-1 text-xs font-semibold text-emerald-700">
                    Active
                  </span>
                )}
              </div>

              {skill.description && (
                <p className="mt-3 line-clamp-3 text-sm leading-6 text-slate-600">
                  {skill.description}
                </p>
              )}

              <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
                <span>Users: {skill.userCount}</span>

                <span>
                  Popularity: {skill.popularityScore}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {skills.length > 0 && onViewAll && (
        <div className="mt-5 text-center sm:hidden">
          <button
            type="button"
            onClick={onViewAll}
            className="w-full rounded-xl border border-slate-300 px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            View all skills
          </button>
        </div>
      )}
    </section>
  );
}
