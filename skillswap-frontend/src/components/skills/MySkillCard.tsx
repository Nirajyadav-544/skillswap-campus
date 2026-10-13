import type { UserSkill } from "../../types/skills/skill.types";

interface MySkillCardProps {
  skill: UserSkill;
  onEdit?: (skill: UserSkill) => void;
  onRemove?: (skill: UserSkill) => void;
}

export default function MySkillCard({
  skill,
  onEdit,
  onRemove,
}: MySkillCardProps) {
  return (
    <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <h3 className="truncate text-lg font-bold text-slate-900">
            {skill.skillName ?? `Skill #${skill.skillId}`}
          </h3>

          {skill.skillCategory && (
            <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-slate-500">
              {skill.skillCategory}
            </p>
          )}
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

      <div className="mt-5 grid grid-cols-2 gap-3">
        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Type</p>
          <p className="mt-1 font-semibold text-slate-900">
            {skill.skillType}
          </p>
        </div>

        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Level</p>
          <p className="mt-1 font-semibold text-slate-900">
            {skill.proficiencyLevel}
          </p>
        </div>

        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Experience</p>
          <p className="mt-1 font-semibold text-slate-900">
            {skill.yearsOfExperience} years
          </p>
        </div>

        <div className="rounded-xl bg-slate-50 p-3">
          <p className="text-xs text-slate-500">Exchanges</p>
          <p className="mt-1 font-semibold text-slate-900">
            {skill.exchangeCount}
          </p>
        </div>
      </div>

      {skill.description && (
        <p className="mt-4 line-clamp-3 text-sm leading-6 text-slate-600">
          {skill.description}
        </p>
      )}

      <div className="mt-5 flex flex-wrap gap-2">
        {skill.canTeach && (
          <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-700">
            Can teach
          </span>
        )}

        {skill.wantsToLearn && (
          <span className="rounded-full bg-violet-100 px-3 py-1 text-xs font-semibold text-violet-700">
            Wants to learn
          </span>
        )}

        {skill.verified && (
          <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700">
            Verified
          </span>
        )}
      </div>

      {(onEdit || onRemove) && (
        <div className="mt-5 flex gap-2 border-t border-slate-100 pt-4">
          {onEdit && (
            <button
              type="button"
              onClick={() => onEdit(skill)}
              className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              Edit
            </button>
          )}

          {onRemove && (
            <button
              type="button"
              onClick={() => onRemove(skill)}
              className="rounded-xl border border-red-200 px-4 py-2 text-sm font-semibold text-red-600 transition hover:bg-red-50"
            >
              Remove
            </button>
          )}
        </div>
      )}
    </article>
  );
}
