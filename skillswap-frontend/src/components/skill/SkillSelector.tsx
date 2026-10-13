 import type { Skill } from "../../types/skill/skill.types";

interface SkillSelectorProps {
  skills: Skill[];
  value: number | null;
  onChange: (skillId: number, skill?: Skill) => void;
  loading?: boolean;
  disabled?: boolean;
  error?: string | null;
  required?: boolean;
  placeholder?: string;
  label?: string;
}

export default function SkillSelector({
  skills,
  value,
  onChange,
  loading = false,
  disabled = false,
  error = null,
  required = false,
  placeholder = "Select a skill",
  label = "Skill",
}: SkillSelectorProps) {
  const handleChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    const selectedId = Number(event.target.value);

    if (!selectedId) {
      onChange(0);
      return;
    }

    const selectedSkill = skills.find(
      (skill) => skill.id === selectedId,
    );

    onChange(selectedId, selectedSkill);
  };

  return (
    <div className="w-full">
      <label
        htmlFor="skill-selector"
        className="mb-2 block text-sm font-medium text-slate-700"
      >
        {label}
        {required && (
          <span className="ml-1 text-red-500">*</span>
        )}
      </label>

      <select
        id="skill-selector"
        value={value ?? ""}
        onChange={handleChange}
        disabled={disabled || loading}
        className={`w-full rounded-lg border bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition ${
          error
            ? "border-red-300 focus:border-red-500 focus:ring-2 focus:ring-red-500/20"
            : "border-slate-300 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
        } ${
          disabled || loading
            ? "cursor-not-allowed bg-slate-100 text-slate-500"
            : ""
        }`}
      >
        <option value="">
          {loading ? "Loading skills..." : placeholder}
        </option>

        {skills.map((skill) => (
          <option key={skill.id} value={skill.id}>
            {skill.name}
          </option>
        ))}
      </select>

      {error && (
        <p className="mt-1.5 text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}