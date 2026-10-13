import { ProficiencyLevel } from "../../types/skill/skill.enums";

interface SkillLevelSelectorProps {
  value: ProficiencyLevel;
  onChange: (value: ProficiencyLevel) => void;
  disabled?: boolean;
  error?: string | null;
  label?: string;
  required?: boolean;
}

const LEVELS: Array<{
  value: ProficiencyLevel;
  label: string;
  description: string;
}> = [
  {
    value: ProficiencyLevel.BEGINNER,
    label: "Beginner",
    description: "Basic understanding and limited practical experience.",
  },
  {
    value: ProficiencyLevel.INTERMEDIATE,
    label: "Intermediate",
    description: "Comfortable using the skill in practical situations.",
  },
  {
    value: ProficiencyLevel.ADVANCED,
    label: "Advanced",
    description: "Strong practical knowledge and independent ability.",
  },
  {
    value: ProficiencyLevel.EXPERT,
    label: "Expert",
    description: "Deep expertise with advanced problem-solving ability.",
  },
];

export function SkillLevelSelector({
  value,
  onChange,
  disabled = false,
  error = null,
  label = "Proficiency level",
  required = true,
}: SkillLevelSelectorProps) {
  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-700">
        {label}
        {required && (
          <span className="ml-1 text-red-500">*</span>
        )}
      </label>

      <div className="grid gap-3 sm:grid-cols-2">
        {LEVELS.map((level) => {
          const selected = value === level.value;

          return (
            <button
              key={level.value}
              type="button"
              disabled={disabled}
              onClick={() => onChange(level.value)}
              className={[
                "rounded-xl border p-4 text-left transition",
                "focus:outline-none focus:ring-2 focus:ring-indigo-200",
                selected
                  ? "border-indigo-500 bg-indigo-50 ring-1 ring-indigo-500"
                  : "border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50",
                disabled
                  ? "cursor-not-allowed opacity-60"
                  : "cursor-pointer",
              ].join(" ")}
            >
              <div className="flex items-center justify-between gap-3">
                <span
                  className={[
                    "text-sm font-semibold",
                    selected
                      ? "text-indigo-700"
                      : "text-slate-900",
                  ].join(" ")}
                >
                  {level.label}
                </span>

                {selected && (
                  <span className="flex h-5 w-5 items-center justify-center rounded-full bg-indigo-600 text-xs font-bold text-white">
                    ✓
                  </span>
                )}
              </div>

              <p className="mt-1.5 text-xs leading-5 text-slate-500">
                {level.description}
              </p>
            </button>
          );
        })}
      </div>

      {error && (
        <p className="text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}

export default SkillLevelSelector;
