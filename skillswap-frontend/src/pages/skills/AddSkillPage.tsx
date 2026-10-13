import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import SkillForm from "../../components/skills/SkillForm";

import skillService from "../../services/skill/skillService";

import type {
  Skill,
  AddSkillRequest,
} from "../../types/skills/skill.types";

const AddSkillPage = () => {
  const navigate = useNavigate();

  const [skills, setSkills] = useState<Skill[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadSkills = async () => {
      try {
        setLoading(true);
        setError("");

        const result =
          await skillService.getActiveSkills();

        setSkills(result);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load available skills.",
        );
      } finally {
        setLoading(false);
      }
    };

    void loadSkills();
  }, []);

  const handleSubmit = async (
    values: AddSkillRequest,
  ) => {
    try {
      setSubmitting(true);
      setError("");

      await skillService.addUserSkill({
        skillId: values.skillId,
        skillType: values.skillType,
        proficiencyLevel:
          values.proficiencyLevel,
        yearsOfExperience:
          values.yearsOfExperience,
        description:
          values.description?.trim() || null,
      });

      navigate("/skills/my");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to add skill.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate("/skills/my");
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto w-full max-w-2xl">

        <button
          type="button"
          onClick={handleCancel}
          className="mb-6 inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-white hover:text-indigo-600"
        >
          <span aria-hidden="true">←</span>
          Back to My Skills
        </button>

        {error && (
          <div
            className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700"
            role="alert"
          >
            {error}
          </div>
        )}

        {loading ? (
          <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <p className="text-sm font-medium text-slate-600">
              Loading available skills...
            </p>
          </section>
        ) : skills.length === 0 ? (
          <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <h1 className="text-xl font-bold text-slate-900">
              No skills available
            </h1>

            <p className="mt-2 text-sm leading-6 text-slate-500">
              There are currently no active skills available
              to add to your profile.
            </p>
          </section>
        ) : (
          <SkillForm
            skills={skills}
            loading={submitting}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
          />
        )}
      </div>
    </main>
  );
};

export default AddSkillPage;