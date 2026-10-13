import { useEffect, useState } from "react";

import {
  ProficiencyLevel,
  SkillType,
} from "../../../types/skill/skill.enums";

import type {
  UpdateUserSkillRequest,
} from "../../../types/skill/skillRequest.types";

import type {
  UserSkill,
} from "../../../types/skill/skill.types";

import SkillLevelSelector from "../SkillLevelSelector";

import skillService from "../../../services/skill/skillService";

interface EditSkillFormProps {
  userSkill: UserSkill;
  onSuccess?: (
    userSkill: UserSkill,
  ) => void;
  onCancel?: () => void;
}

interface FormErrors {
  skillType?: string;
  proficiencyLevel?: string;
  yearsOfExperience?: string;
  description?: string;
  submit?: string;
}

const MAX_DESCRIPTION_LENGTH = 3000;
const MAX_YEARS_OF_EXPERIENCE = 80;

export function EditSkillForm({
  userSkill,
  onSuccess,
  onCancel,
}: EditSkillFormProps) {
  const [skillType, setSkillType] =
    useState<SkillType>(userSkill.skillType);

  const [proficiencyLevel, setProficiencyLevel] =
    useState<ProficiencyLevel>(
      userSkill.proficiencyLevel,
    );

  const [yearsOfExperience, setYearsOfExperience] =
    useState(
      String(userSkill.yearsOfExperience),
    );

  const [description, setDescription] =
    useState(userSkill.description ?? "");

  const [active, setActive] =
    useState(userSkill.active);

  const [errors, setErrors] =
    useState<FormErrors>({});

  const [submitting, setSubmitting] =
    useState(false);

  useEffect(() => {
    setSkillType(userSkill.skillType);
    setProficiencyLevel(
      userSkill.proficiencyLevel,
    );
    setYearsOfExperience(
      String(userSkill.yearsOfExperience),
    );
    setDescription(userSkill.description ?? "");
    setActive(userSkill.active);
    setErrors({});
  }, [userSkill]);

  const validate = (): boolean => {
    const nextErrors: FormErrors = {};

    const parsedYears =
      Number(yearsOfExperience);

    if (
      !Number.isFinite(parsedYears) ||
      parsedYears < 0 ||
      parsedYears > MAX_YEARS_OF_EXPERIENCE
    ) {
      nextErrors.yearsOfExperience =
        "Years of experience must be between 0 and 80.";
    }

    if (description.length > MAX_DESCRIPTION_LENGTH) {
      nextErrors.description =
        "Description cannot exceed 3000 characters.";
    }

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    if (!validate()) {
      return;
    }

    const request: UpdateUserSkillRequest = {
      skillType,
      proficiencyLevel,
      yearsOfExperience:
        Number(yearsOfExperience),
      description:
        description.trim() || null,
      active,
    };

    setSubmitting(true);
    setErrors({});

    try {
      const updated =
        await skillService.updateUserSkill(
          userSkill.id,
          request,
        );

      onSuccess?.(updated);
    } catch (error) {
      setErrors({
        submit:
          error instanceof Error
            ? error.message
            : "Failed to update skill.",
      });
    } finally {
      setSubmitting(false);
    }
  };

  const formattedCategory =
    userSkill.skillCategory
      ? userSkill.skillCategory
          .replace(/_/g, " ")
          .toLowerCase()
          .replace(/\b\w/g, (letter) =>
            letter.toUpperCase(),
          )
      : "Uncategorized";

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-6 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
    >
      <div>
        <h2 className="text-xl font-semibold text-slate-900">
          Edit Skill
        </h2>

        <p className="mt-1 text-sm text-slate-500">
          Update your information for{" "}
          <span className="font-medium text-slate-700">
            {userSkill.skillName}
          </span>
          .
        </p>
      </div>

      <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
        <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
          Skill
        </p>

        <p className="mt-1 text-base font-semibold text-slate-900">
          {userSkill.skillName}
        </p>

        <p className="mt-1 text-sm text-slate-500">
          {formattedCategory}
        </p>
      </div>

      <fieldset disabled={submitting}>
        <legend className="mb-3 text-sm font-medium text-slate-700">
          Skill type
        </legend>

        <div className="grid gap-3 sm:grid-cols-3">
          {[
            {
              value: SkillType.TEACH,
              label: "Teach",
              description:
                "I can teach this skill.",
            },
            {
              value: SkillType.LEARN,
              label: "Learn",
              description:
                "I want to learn this skill.",
            },
            {
              value: SkillType.BOTH,
              label: "Both",
              description:
                "I can teach and want to learn.",
            },
          ].map((option) => (
            <button
              key={option.value}
              type="button"
              onClick={() =>
                setSkillType(option.value)
              }
              className={[
                "rounded-xl border p-4 text-left transition",
                skillType === option.value
                  ? "border-indigo-500 bg-indigo-50"
                  : "border-slate-200 hover:bg-slate-50",
              ].join(" ")}
            >
              <p className="text-sm font-semibold text-slate-900">
                {option.label}
              </p>

              <p className="mt-1 text-xs leading-5 text-slate-500">
                {option.description}
              </p>
            </button>
          ))}
        </div>
      </fieldset>

      <SkillLevelSelector
        value={proficiencyLevel}
        onChange={setProficiencyLevel}
        disabled={submitting}
        error={errors.proficiencyLevel}
      />

      <div>
        <label
          htmlFor="edit-skill-years"
          className="block text-sm font-medium text-slate-700"
        >
          Years of experience
        </label>

        <input
          id="edit-skill-years"
          type="number"
          min="0"
          max="80"
          step="0.1"
          value={yearsOfExperience}
          onChange={(event) =>
            setYearsOfExperience(event.target.value)
          }
          disabled={submitting}
          className={[
            "mt-1.5 w-full rounded-xl border px-4 py-2.5 text-sm",
            "outline-none transition",
            "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100",
            errors.yearsOfExperience
              ? "border-red-400"
              : "border-slate-300",
          ].join(" ")}
        />

        {errors.yearsOfExperience && (
          <p className="mt-1.5 text-sm text-red-600">
            {errors.yearsOfExperience}
          </p>
        )}
      </div>

      <div>
        <div className="flex items-center justify-between gap-3">
          <label
            htmlFor="edit-skill-description"
            className="block text-sm font-medium text-slate-700"
          >
            Description
          </label>

          <span className="text-xs text-slate-400">
            {description.length}/3000
          </span>
        </div>

        <textarea
          id="edit-skill-description"
          value={description}
          onChange={(event) =>
            setDescription(event.target.value)
          }
          maxLength={MAX_DESCRIPTION_LENGTH}
          rows={4}
          disabled={submitting}
          placeholder="Describe your experience or learning goals..."
          className={[
            "mt-1.5 w-full resize-y rounded-xl border px-4 py-3 text-sm",
            "outline-none transition",
            "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100",
            errors.description
              ? "border-red-400"
              : "border-slate-300",
          ].join(" ")}
        />

        {errors.description && (
          <p className="mt-1.5 text-sm text-red-600">
            {errors.description}
          </p>
        )}
      </div>

      <div className="rounded-xl border border-slate-200 p-4">
        <label className="flex cursor-pointer items-center justify-between gap-4">
          <div>
            <p className="text-sm font-medium text-slate-900">
              Active skill
            </p>

            <p className="mt-1 text-xs text-slate-500">
              Active skills are available for your
              current SkillSwap profile.
            </p>
          </div>

          <input
            type="checkbox"
            checked={active}
            onChange={(event) =>
              setActive(event.target.checked)
            }
            disabled={submitting}
            className="h-5 w-5 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
          />
        </label>
      </div>

      {errors.submit && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
          {errors.submit}
        </div>
      )}

      <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            disabled={submitting}
            className="rounded-xl border border-slate-300 px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-50"
          >
            Cancel
          </button>
        )}

        <button
          type="submit"
          disabled={submitting}
          className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {submitting
            ? "Saving..."
            : "Save Changes"}
        </button>
      </div>
    </form>
  );
}

export default EditSkillForm;
