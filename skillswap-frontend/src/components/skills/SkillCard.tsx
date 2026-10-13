import type { Skill } from "../../types/skills/skill.types";

interface SkillCardProps {
  skill: Skill;
  onSelect?: (skill: Skill) => void;
}

export default function SkillCard({
  skill,
  onSelect,
}: SkillCardProps) {
  return (
    <article
      className={`rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition ${
        onSelect
          ? "cursor-pointer hover:-translate-y-0.5 hover:border-slate-300 hover:shadow-md"
          : ""
      }`}
      onClick={() => onSelect?.(skill)}
      role={onSelect ? "button" : undefined}
      tabIndex={onSelect ? 0 : undefined}
      onKeyDown={(event) => {
        if (onSelect && (event.key === "Enter" || event.key === " ")) {
          event.preventDefault();
          onSelect(skill);
        }
      }}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h3 className="truncate text-lg font-bold text-slate-900">
            {skill.name}
          </h3>

          <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-slate-500">
            {skill.category}
          </p>
        </div>

        <span
          className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${
            skill.active
              ? "bg-emerald-100 text-emerald-700"
              : "bg-slate-100 text-slate-500"
          }`}
        >
          {skill.active ? "Active" : "Inactive"}
        </span>
      </div>

      {skill.description && (
        <p className="mt-4 line-clamp-3 text-sm leading-6 text-slate-600">
          {skill.description}
        </p>
      )}

      <div className="mt-5 grid grid-cols-2 gap-3">
        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Users</p>
          <p className="mt-1 font-bold text-slate-900">
            {skill.userCount}
          </p>
        </div>

        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Popularity</p>
          <p className="mt-1 font-bold text-slate-900">
            {skill.popularityScore}
          </p>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
        <span>
          {skill.systemDefined ? "System skill" : "Custom skill"}
        </span>

        <span>{skill.active ? "Available" : "Unavailable"}</span>
      </div>
    </article>
  );
}
