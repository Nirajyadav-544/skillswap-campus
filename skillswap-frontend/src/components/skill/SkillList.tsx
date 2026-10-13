import type {
  Skill,
  SkillSummary,
} from "../../types/skill/skill.types";

import SkillCard from "./SkillCard";

type SkillListItem = Skill | SkillSummary;

interface SkillListProps {
  skills: SkillListItem[];
  loading?: boolean;
  error?: string | null;
  emptyMessage?: string;
  selectedSkillId?: number | null;
  selectable?: boolean;
  showDescription?: boolean;
  showPopularity?: boolean;
  showUserCount?: boolean;
  onSkillClick?: (skill: SkillListItem) => void;
  onSkillSelect?: (skill: SkillListItem) => void;
}

export function SkillList({
  skills,
  loading = false,
  error = null,
  emptyMessage = "No skills found.",
  selectedSkillId = null,
  selectable = false,
  showDescription = true,
  showPopularity = false,
  showUserCount = false,
  onSkillClick,
  onSkillSelect,
}: SkillListProps) {
  if (loading) {
    return (
      <div
        className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3"
        aria-label="Loading skills"
      >
        {Array.from({ length: 6 }).map((_, index) => (
          <div
            key={index}
            className="animate-pulse rounded-xl border border-slate-200 bg-white p-4 shadow-sm"
          >
            <div className="h-5 w-2/3 rounded bg-slate-200" />
            <div className="mt-3 h-4 w-1/3 rounded bg-slate-100" />
            <div className="mt-4 h-12 rounded bg-slate-100" />
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
        {error}
      </div>
    );
  }

  if (skills.length === 0) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
        <p className="text-sm text-slate-500">
          {emptyMessage}
        </p>
      </div>
    );
  }

  return (
    <div
      className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3"
      role={selectable ? "listbox" : undefined}
    >
      {skills.map((skill) => (
        <SkillCard
          key={skill.id}
          skill={skill}
          selected={
            selectedSkillId !== null &&
            skill.id === selectedSkillId
          }
          showDescription={showDescription}
          showPopularity={showPopularity}
          showUserCount={showUserCount}
          onClick={onSkillClick}
          onSelect={
            selectable
              ? onSkillSelect
              : undefined
          }
        />
      ))}
    </div>
  );
}

export default SkillList;
