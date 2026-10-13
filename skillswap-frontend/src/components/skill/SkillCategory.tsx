import type { SkillCategory as SkillCategoryValue } from "../../types/skill/skill.enums";

interface SkillCategoryProps {
  value?: SkillCategoryValue | null;
  onChange?: (value: SkillCategoryValue) => void;
  categories: readonly SkillCategoryValue[];
  disabled?: boolean;
  error?: string | null;
  label?: string;
  required?: boolean;
  placeholder?: string;
}

export function SkillCategorySelector({
  value = null,
  onChange,
  categories,
  disabled = false,
  error = null,
  label = "Skill category",
  required = false,
  placeholder = "Select category",
}: SkillCategoryProps) {
  const formatCategory = (
    category: string,
  ): string => {
    return category
      .replace(/_/g, " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) =>
        letter.toUpperCase(),
      );
  };

  return (
    <div className="space-y-1.5">
      <label className="block text-sm font-medium text-slate-700">
        {label}
        {required && (
          <span className="ml-1 text-red-500">*</span>
        )}
      </label>

      <select
        value={value ?? ""}
        disabled={disabled}
        onChange={(event) => {
          const nextValue =
            event.target.value as SkillCategoryValue;

          if (nextValue) {
            onChange?.(nextValue);
          }
        }}
        className={[
          "w-full rounded-xl border bg-white px-4 py-2.5 text-sm text-slate-900",
          "outline-none transition",
          "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100",
          disabled
            ? "cursor-not-allowed bg-slate-100 opacity-70"
            : "border-slate-300",
          error
            ? "border-red-400"
            : "",
        ].join(" ")}
      >
        <option value="">
          {placeholder}
        </option>

        {categories.map((category) => (
          <option
            key={category}
            value={category}
          >
            {formatCategory(category)}
          </option>
        ))}
      </select>

      {error && (
        <p className="text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}

export default SkillCategorySelector;
