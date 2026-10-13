import { useEffect, useState } from "react";

import {
  ProficiencyLevel,
  SkillType,
} from "../../../types/skill/skill.enums";

import type { AddUserSkillRequest } from "../../../types/skill/skillRequest.types";
import type { Skill } from "../../../types/skill/skill.types";

import SkillLevelSelector from "../SkillLevelSelector";
import SkillSearch from "../SkillSearch";
import SkillSelector from "../SkillSelector";

import skillService from "../../../services/skill/skillService";

interface AddSkillFormProps {
  skills: Skill[];
  loadingSkills?: boolean;
  onSuccess?: (userSkill: Awaited<ReturnType<typeof skillService.addUserSkill>>) => void;
  onCancel?: () => void;
  initialSkillId?: number | null;
}

interface FormErrors {
  skillId?: string;
  skillType?: string;
  proficiencyLevel?: string;
  yearsOfExperience?: string;
  description?: string;
  submit?: string;
}

const MAX_DESCRIPTION_LENGTH = 3000;
const MAX_YEARS_OF_EXPERIENCE = 80;

export function AddSkillForm({
  skills,
  loadingSkills = false,
  onSuccess,
  onCancel,
  initialSkillId = null,
}: AddSkillFormProps) {
  const [skillId, setSkillId] =
    useState<number | null>(initialSkillId);

  const [skillType, setSkillType] =
    useState<SkillType>(SkillType.BOTH);

  const [proficiencyLevel, setProficiencyLevel] =
    useState<ProficiencyLevel>(
      ProficiencyLevel.BEGINNER,
    );

  const [yearsOfExperience, setYearsOfExperience] =
    useState("0");

  const [description, setDescription] =
    useState("");

  const [errors, setErrors] =
    useState<FormErrors>({});

  const [submitting, setSubmitting] =
    useState(false);

  const [searchMode, setSearchMode] =
    useState(false);

  useEffect(() => {
    setSkillId(initialSkillId);
  }, [initialSkillId]);

  const validate = (): boolean => {
    const nextErrors: FormErrors = {};

    if (skillId === null) {
      nextErrors.skillId = "Please select a skill.";
    }

    if (!skillType) {
      nextErrors.skillType = "Please select a skill type.";
    }

    if (!proficiencyLevel) {
      nextErrors.proficiencyLevel =
        "Please select a proficiency level.";
    }

    const parsedYears = Number(yearsOfExperience);

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

    if (!validate() || skillId === null) {
      return;
    }

    const parsedYears =
      Number(yearsOfExperience);

    const request: AddUserSkillRequest = {
      skillId,
      skillType,
      proficiencyLevel,
      yearsOfExperience: parsedYears,
      description:
        description.trim() || null,
    };

    setSubmitting(true);
    setErrors((current) => ({
      ...current,
      submit: undefined,
    }));

    try {
      const result =
        await skillService.addUserSkill(request);

      onSuccess?.(result);
    } catch (error) {
      setErrors((current) => ({
        ...current,
        submit:
          error instanceof Error
            ? error.message
            : "Failed to add skill.",
      }));
    } finally {
      setSubmitting(false);
    }
  };

  const handleSearchSelect = (
    selectedSkill: {
      id: number;
      name: string;
    },
  ) => {
    const matchingSkill = skills.find(
      (skill) => skill.id === selectedSkill.id,
    );

    if (matchingSkill) {
      setSkillId(matchingSkill.id);
      setSearchMode(false);
      setErrors((current) => ({
        ...current,
        skillId: undefined,
      }));
    }
  };

  const selectedSkill =
    skills.find(
      (skill) => skill.id === skillId,
    ) ?? null;

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-6 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
    >
      <div>
        <h2 className="text-xl font-semibold text-slate-900">
          Add Skill
        </h2>

        <p className="mt-1 text-sm text-slate-500">
          Add a skill to your Skill DNA with your
          current experience and learning preference.
        </p>
      </div>

      <div className="space-y-3">
        <div className="flex items-center justify-between gap-3">
          <label className="block text-sm font-medium text-slate-700">
            Skill
            <span className="ml-1 text-red-500">*</span>
          </label>

          <button
            type="button"
            onClick={() =>
              setSearchMode((current) => !current)
            }
            disabled={submitting}
            className="text-sm font-medium text-indigo-600 hover:text-indigo-700 disabled:opacity-50"
          >
            {searchMode
              ? "Browse skills"
              : "Search skills"}
          </button>
        </div>

        {searchMode ? (
          <SkillSearch
            label=""
            placeholder="Search for a skill..."
            onSelect={handleSearchSelect}
            disabled={submitting}
          />
        ) : (
          <SkillSelector
            skills={skills}
            value={skillId}
            onChange={(nextSkillId) => {
              setSkillId(nextSkillId);
              setErrors((current) => ({
                ...current,
                skillId: undefined,
              }));
            }}
            loading={loadingSkills}
            disabled={submitting}
            error={errors.skillId}
            required
          />
        )}

        {selectedSkill && searchMode && (
          <p className="text-sm text-slate-600">
            Selected:{" "}
            <span className="font-medium">
              {selectedSkill.name}
            </span>
          </p>
        )}

        {searchMode && errors.skillId && (
          <p className="text-sm text-red-600">
            {errors.skillId}
          </p>
        )}
      </div>

      <fieldset disabled={submitting}>
        <legend className="mb-3 text-sm font-medium text-slate-700">
          What do you want to do with this skill?
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

        {errors.skillType && (
          <p className="mt-2 text-sm text-red-600">
            {errors.skillType}
          </p>
        )}
      </fieldset>

      <SkillLevelSelector
        value={proficiencyLevel}
        onChange={setProficiencyLevel}
        disabled={submitting}
        error={errors.proficiencyLevel}
      />

      <div>
        <label
          htmlFor="skill-years-of-experience"
          className="block text-sm font-medium text-slate-700"
        >
          Years of experience
          <span className="ml-1 text-red-500">*</span>
        </label>

        <input
          id="skill-years-of-experience"
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

        {errors.yearsOfExperience ? (
          <p className="mt-1.5 text-sm text-red-600">
            {errors.yearsOfExperience}
          </p>
        ) : (
          <p className="mt-1.5 text-xs text-slate-500">
            Enter a value between 0 and 80 years.
          </p>
        )}
      </div>

      <div>
        <div className="flex items-center justify-between gap-3">
          <label
            htmlFor="skill-description"
            className="block text-sm font-medium text-slate-700"
          >
            Description
          </label>

          <span className="text-xs text-slate-400">
            {description.length}/3000
          </span>
        </div>

        <textarea
          id="skill-description"
          value={description}
          onChange={(event) =>
            setDescription(event.target.value)
          }
          maxLength={MAX_DESCRIPTION_LENGTH}
          rows={4}
          disabled={submitting}
          placeholder="Describe your experience, strengths, or learning goals..."
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
          disabled={submitting || loadingSkills}
          className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {submitting
            ? "Adding..."
            : "Add Skill"}
        </button>
      </div>
    </form>
  );
}

export default AddSkillForm;
