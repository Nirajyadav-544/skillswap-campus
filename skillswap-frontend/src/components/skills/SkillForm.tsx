import {
  useEffect,
  useState,
  type FormEvent,
} from "react";

import type {
  AddUserSkillRequest,
  ProficiencyLevel,
  Skill,
  SkillType,
} from "../../types/skills/skill.types";

interface SkillFormProps {
  skills: Skill[];
  initialValues?: Partial<AddUserSkillRequest>;
  loading?: boolean;
  onSubmit: (
    values: AddUserSkillRequest
  ) => void | Promise<void>;
  onCancel?: () => void;
}

const skillTypes: SkillType[] = [
  "TEACH",
  "LEARN",
  "BOTH",
];

const proficiencyLevels: ProficiencyLevel[] = [
  "BEGINNER",
  "INTERMEDIATE",
  "ADVANCED",
  "EXPERT",
];

export default function SkillForm({
  skills,
  initialValues,
  loading = false,
  onSubmit,
  onCancel,
}: SkillFormProps) {
  const [skillId, setSkillId] = useState<number>(
    initialValues?.skillId ?? 0
  );

  const [skillType, setSkillType] = useState<SkillType>(
    initialValues?.skillType ?? "TEACH"
  );

  const [proficiencyLevel, setProficiencyLevel] =
    useState<ProficiencyLevel>(
      initialValues?.proficiencyLevel ?? "BEGINNER"
    );

  const [experience, setExperience] = useState<number>(
    initialValues?.yearsOfExperience ?? 0
  );

  const [description, setDescription] = useState<string>(
    initialValues?.description ?? ""
  );

  const [error, setError] = useState<string>("");

  useEffect(() => {
    setSkillId(initialValues?.skillId ?? 0);

    setSkillType(
      initialValues?.skillType ?? "TEACH"
    );

    setProficiencyLevel(
      initialValues?.proficiencyLevel ?? "BEGINNER"
    );

    setExperience(
      initialValues?.yearsOfExperience ?? 0
    );

    setDescription(
      initialValues?.description ?? ""
    );
  }, [initialValues]);

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();
    setError("");

    if (!skillId) {
      setError("Please select a skill.");
      return;
    }

    if (experience < 0 || experience > 80) {
      setError(
        "Years of experience must be between 0 and 80."
      );
      return;
    }

    await onSubmit({
      skillId,
      skillType,
      proficiencyLevel,
      yearsOfExperience: experience,
      description: description.trim() || undefined,
    });
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-6 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
    >
      <div>
        <h2 className="text-xl font-bold text-slate-900">
          Add Skill
        </h2>

        <p className="mt-1 text-sm text-slate-500">
          Add a skill to your SkillSwap profile.
        </p>
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
          {error}
        </div>
      )}

      <div>
        <label
          htmlFor="skillId"
          className="mb-2 block text-sm font-semibold text-slate-700"
        >
          Skill
        </label>

        <select
          id="skillId"
          value={skillId}
          onChange={(event) =>
            setSkillId(Number(event.target.value))
          }
          disabled={loading}
          className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
        >
          <option value={0}>
            Select a skill
          </option>

          {skills.map((skill) => (
            <option
              key={skill.id}
              value={skill.id}
            >
              {skill.name}
            </option>
          ))}
        </select>
      </div>

      <div className="grid gap-5 md:grid-cols-2">
        <div>
          <label
            htmlFor="skillType"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Skill Type
          </label>

          <select
            id="skillType"
            value={skillType}
            onChange={(event) =>
              setSkillType(
                event.target.value as SkillType
              )
            }
            disabled={loading}
            className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
          >
            {skillTypes.map((type) => (
              <option
                key={type}
                value={type}
              >
                {type}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label
            htmlFor="proficiencyLevel"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Proficiency Level
          </label>

          <select
            id="proficiencyLevel"
            value={proficiencyLevel}
            onChange={(event) =>
              setProficiencyLevel(
                event.target.value as ProficiencyLevel
              )
            }
            disabled={loading}
            className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
          >
            {proficiencyLevels.map((level) => (
              <option
                key={level}
                value={level}
              >
                {level}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div>
        <label
          htmlFor="yearsOfExperience"
          className="mb-2 block text-sm font-semibold text-slate-700"
        >
          Years of Experience
        </label>

        <input
          id="yearsOfExperience"
          type="number"
          min="0"
          max="80"
          step="0.1"
          value={experience}
          onChange={(event) =>
            setExperience(
              Number(event.target.value)
            )
          }
          disabled={loading}
          className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
        />
      </div>

      <div>
        <label
          htmlFor="skillDescription"
          className="mb-2 block text-sm font-semibold text-slate-700"
        >
          Description
        </label>

        <textarea
          id="skillDescription"
          value={description}
          onChange={(event) =>
            setDescription(event.target.value)
          }
          maxLength={3000}
          rows={5}
          disabled={loading}
          placeholder="Describe your experience, interests, or what you want to learn..."
          className="w-full resize-y rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
        />

        <p className="mt-1 text-right text-xs text-slate-400">
          {description.length}/3000
        </p>
      </div>

      <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            disabled={loading}
            className="rounded-xl border border-slate-300 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Cancel
          </button>
        )}

        <button
          type="submit"
          disabled={loading}
          className="rounded-xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? "Saving..." : "Save Skill"}
        </button>
      </div>
    </form>
  );
}