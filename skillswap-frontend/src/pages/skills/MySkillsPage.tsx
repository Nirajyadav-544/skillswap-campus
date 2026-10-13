import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import MySkillCard from "../../components/skills/MySkillCard";
import skillService from "../../services/skill/skillService";

import type { MySkill } from "../../types/skills/skill.types";

const MySkillsPage = () => {
  const navigate = useNavigate();

  const [skills, setSkills] = useState<MySkill[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const loadMySkills = async () => {
    try {
      setLoading(true);
      setError("");

      const result = await skillService.getMySkills();

      setSkills(result);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load your skills.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadMySkills();
  }, []);

  const handleRemove = async (skill: MySkill) => {
    const skillName =
      skill.skillName ?? `Skill #${skill.skillId}`;

    const confirmed = window.confirm(
      `Are you sure you want to remove "${skillName}" from your skills?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(skill.id);
      setError("");

      await skillService.deleteUserSkill(skill.id);

      setSkills((currentSkills) =>
        currentSkills.filter(
          (currentSkill) =>
            currentSkill.id !== skill.id,
        ),
      );
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete skill.",
      );
    } finally {
      setDeletingId(null);
    }
  };

  const handleEdit = (skill: MySkill) => {
    navigate(`/skills/edit/${skill.id}`);
  };

  const handleAddSkill = () => {
    navigate("/skills/add");
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-7xl">

        {/* Header */}
        <div className="mb-8 flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-sm font-semibold text-indigo-600">
              SkillSwap Campus
            </p>

            <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">
              My Skills
            </h1>

            <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
              Manage the skills that represent your expertise
              and build your Skill DNA.
            </p>
          </div>

          <button
            type="button"
            onClick={handleAddSkill}
            className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
          >
            + Add Skill
          </button>
        </div>

        {/* Error */}
        {error && (
          <div
            className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700"
            role="alert"
          >
            {error}
          </div>
        )}

        {/* Loading */}
        {loading ? (
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {Array.from({ length: 4 }).map((_, index) => (
              <div
                key={index}
                className="h-64 animate-pulse rounded-2xl bg-white shadow-sm"
              />
            ))}
          </div>
        ) : skills.length === 0 ? (
          /* Empty state */
          <section className="rounded-3xl border border-dashed border-slate-300 bg-white px-6 py-16 text-center shadow-sm">
            <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-50 text-2xl text-indigo-600">
              ✦
            </div>

            <h2 className="mt-5 text-xl font-bold text-slate-900">
              Your skill profile is empty
            </h2>

            <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
              Add your first skill to start building your
              Skill DNA and prepare your profile for future
              matching.
            </p>

            <button
              type="button"
              onClick={handleAddSkill}
              className="mt-6 rounded-xl bg-slate-900 px-6 py-3 text-sm font-semibold text-white transition hover:bg-indigo-600"
            >
              Add your first skill
            </button>
          </section>
        ) : (
          /* Skills */
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {skills.map((skill) => (
              <div
                key={skill.id}
                className={
                  deletingId === skill.id
                    ? "pointer-events-none opacity-50"
                    : ""
                }
              >
                <MySkillCard
                  skill={skill}
                  onRemove={handleRemove}
                  onEdit={handleEdit}
                />
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  );
};

export default MySkillsPage;