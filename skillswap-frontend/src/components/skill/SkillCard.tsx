import type { Skill, SkillSummary } from "../../types/skill/skill.types";
import {
  ProficiencyLevel,
  SkillType,
} from "../../types/skill/skill.enums";

type SkillCardSkill = Skill | SkillSummary;

interface SkillCardProps {
  skill: SkillCardSkill;
  selected?: boolean;
  disabled?: boolean;
  showDescription?: boolean;
  showPopularity?: boolean;
  showUserCount?: boolean;
  skillType?: SkillType;
  proficiencyLevel?: ProficiencyLevel;
  yearsOfExperience?: number;
  onClick?: (skill: SkillCardSkill) => void;
  onSelect?: (skill: SkillCardSkill) => void;
}

const getProficiencyLabel = (
  level: ProficiencyLevel,
): string => {
  switch (level) {
    case ProficiencyLevel.BEGINNER:
      return "Beginner";
    case ProficiencyLevel.INTERMEDIATE:
      return "Intermediate";
    case ProficiencyLevel.ADVANCED:
      return "Advanced";
    case ProficiencyLevel.EXPERT:
      return "Expert";
    default:
      return level;
  }
};

const getSkillTypeLabel = (
  type: SkillType,
): string => {
  switch (type) {
    case SkillType.TEACH:
      return "Teach";
    case SkillType.LEARN:
      return "Learn";
    case SkillType.BOTH:
      return "Teach & Learn";
    default:
      return type;
  }
};

const isFullSkill = (
  skill: SkillCardSkill,
): skill is Skill => {
  return "description" in skill;
};

const getCategoryLabel = (
  category: string,
): string => {
  return category
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
};

export function SkillCard({
  skill,
  selected = false,
  disabled = false,
  showDescription = true,
  showPopularity = false,
  showUserCount = false,
  skillType,
  proficiencyLevel,
  yearsOfExperience,
  onClick,
  onSelect,
}: SkillCardProps) {
  const handleClick = () => {
    if (disabled) {
      return;
    }

    onClick?.(skill);
    onSelect?.(skill);
  };

  const handleKeyDown = (
    event: React.KeyboardEvent<HTMLDivElement>,
  ) => {
    if (disabled) {
      return;
    }

    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      handleClick();
    }
  };

  const description = isFullSkill(skill)
    ? skill.description
    : undefined;

  return (
    <div
      role={onClick || onSelect ? "button" : undefined}
      tabIndex={
        !disabled && (onClick || onSelect)
          ? 0
          : undefined
      }
      aria-selected={selected}
      aria-disabled={disabled}
      onClick={handleClick}
      onKeyDown={handleKeyDown}
      className={[
        "rounded-xl border bg-white p-4 shadow-sm transition",
        onClick || onSelect
          ? "cursor-pointer hover:shadow-md"
          : "",
        selected
          ? "border-indigo-500 ring-2 ring-indigo-100"
          : "border-slate-200",
        disabled
          ? "cursor-not-allowed opacity-60"
          : "",
      ].join(" ")}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="truncate text-base font-semibold text-slate-900">
              {skill.name}
            </h3>

            {isFullSkill(skill) && skill.systemDefined && (
              <span className="rounded-full bg-indigo-50 px-2 py-0.5 text-xs font-medium text-indigo-700">
                System
              </span>
            )}

            {!skill.active && (
              <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-600">
                Inactive
              </span>
            )}
          </div>

          <p className="mt-1 text-sm text-slate-500">
            {getCategoryLabel(skill.category)}
          </p>
        </div>

        {selected && (
          <span
            aria-label="Selected"
            className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-indigo-600 text-sm font-bold text-white"
          >
            ✓
          </span>
        )}
      </div>

      {showDescription && description && (
        <p className="mt-3 line-clamp-3 text-sm leading-6 text-slate-600">
          {description}
        </p>
      )}

      {(showPopularity || showUserCount) && (
        <div className="mt-4 flex flex-wrap gap-2">
          {showPopularity && (
            <span className="rounded-lg bg-slate-50 px-2.5 py-1 text-xs text-slate-600">
              Popularity: {skill.popularityScore}
            </span>
          )}

          {showUserCount && (
            <span className="rounded-lg bg-slate-50 px-2.5 py-1 text-xs text-slate-600">
              {skill.userCount} users
            </span>
          )}
        </div>
      )}

      {(skillType ||
        proficiencyLevel ||
        yearsOfExperience !== undefined) && (
        <div className="mt-4 flex flex-wrap gap-2">
          {skillType && (
            <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
              {getSkillTypeLabel(skillType)}
            </span>
          )}

          {proficiencyLevel && (
            <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700">
              {getProficiencyLabel(proficiencyLevel)}
            </span>
          )}

          {yearsOfExperience !== undefined && (
            <span className="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-medium text-amber-700">
              {yearsOfExperience}{" "}
              {yearsOfExperience === 1 ? "year" : "years"}
            </span>
          )}
        </div>
      )}
    </div>
  );
}

export default SkillCard;
