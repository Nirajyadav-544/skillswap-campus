import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import EditSkillForm from "../../components/skill/forms/EditSkillForm";
import skillService from "../../services/skill/skillService";

import type { UserSkill } from "../../types/skill/skill.types";

export default function EditSkillPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [skill, setSkill] = useState<UserSkill | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const loadSkill = async () => {
      if (!id) {
        setError("Skill ID is missing.");
        setLoading(false);
        return;
      }

      const skillId = Number(id);

      if (!Number.isInteger(skillId) || skillId <= 0) {
        setError("Invalid skill ID.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const result =
          await skillService.getMySkillById(skillId);

        setSkill(result);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load skill.",
        );
      } finally {
        setLoading(false);
      }
    };

    void loadSkill();
  }, [id]);

  const handleSuccess = (updatedSkill: UserSkill) => {
    setSkill(updatedSkill);
    setSuccess(true);

    window.setTimeout(() => {
      navigate("/skills/my");
    }, 1000);
  };

  const handleCancel = () => {
    navigate("/skills/my");
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto w-full max-w-3xl">

        <div className="mb-6">
          <button
            type="button"
            onClick={handleCancel}
            className="text-sm font-semibold text-indigo-600 transition hover:text-indigo-700"
          >
            ← Back to My Skills
          </button>
        </div>

        {loading && (
          <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <p className="text-sm font-medium text-slate-600">
              Loading skill...
            </p>
          </section>
        )}

        {!loading && error && (
          <section className="rounded-2xl border border-red-200 bg-red-50 p-6 shadow-sm">
            <h1 className="text-lg font-semibold text-red-800">
              Unable to load skill
            </h1>

            <p className="mt-2 text-sm text-red-700">
              {error}
            </p>

            <button
              type="button"
              onClick={handleCancel}
              className="mt-5 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-600"
            >
              Back to My Skills
            </button>
          </section>
        )}

        {!loading && !error && skill && (
          <>
            <div className="mb-6">
              <p className="text-sm font-semibold text-indigo-600">
                SkillSwap Campus
              </p>

              <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">
                Edit Your Skill
              </h1>

              <p className="mt-2 text-sm leading-6 text-slate-500">
                Update your skill type, proficiency,
                experience, description, and active status.
              </p>
            </div>

            {success && (
              <div
                className="mb-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-700"
                role="status"
              >
                Skill updated successfully. Returning to My
                Skills...
              </div>
            )}

            <EditSkillForm
              userSkill={skill}
              onSuccess={handleSuccess}
              onCancel={handleCancel}
            />
          </>
        )}
      </div>
    </main>
  );
}