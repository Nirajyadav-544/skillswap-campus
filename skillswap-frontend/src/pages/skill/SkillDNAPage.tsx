import { useEffect } from "react";

import useUserSkills from "../../hooks/skill/useUserSkills";

export default function SkillDNAPage() {
  const {
    skills,
    loading,
    error,
    loadSkills,
  } = useUserSkills();

  useEffect(() => {
    void loadSkills();
  }, [loadSkills]);

  return (
    <main className="min-h-screen bg-slate-50">
      <div className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8">

        <section className="mb-8">
          <p className="mb-2 text-sm font-semibold uppercase tracking-wide text-indigo-600">
            SkillSwap Campus
          </p>

          <h1 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
            Your Skill DNA
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-600 sm:text-base">
            Your personalized overview of skills, experience,
            proficiency, and learning activity.
          </p>
        </section>

        {error && (
          <div
            className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700"
            role="alert"
          >
            {error}
          </div>
        )}

        {loading ? (
          <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <p className="text-sm font-medium text-slate-600">
              Loading your Skill DNA...
            </p>
          </div>
        ) : skills.length === 0 ? (
          <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900">
              Your Skill DNA is waiting to be built
            </h2>

            <p className="mx-auto mt-2 max-w-xl text-sm leading-6 text-slate-600">
              Add your skills first. Your Skill DNA will grow as
              you add skills, experience, proficiency, and
              learning interests.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">

            <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <p className="text-sm font-semibold text-indigo-600">
                Skills
              </p>

              <h2 className="mt-1 text-2xl font-bold text-slate-900">
                {skills.length}
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                Skills currently included in your Skill DNA
              </p>
            </section>

            <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <p className="text-sm font-semibold text-indigo-600">
                Skill Profile
              </p>

              <p className="mt-2 text-sm leading-6 text-slate-600">
                Your skill profile is generated from the skills
                you have added to SkillSwap Campus.
              </p>
            </section>

            <section className="lg:col-span-2">
              <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                <h2 className="text-xl font-bold text-slate-900">
                  Your Skills
                </h2>

                <div className="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {skills.map((skill) => (
                    <div
                      key={skill.id}
                      className="rounded-xl border border-slate-200 bg-slate-50 p-4"
                    >
                      <h3 className="font-semibold text-slate-900">
                        {skill.skillName || "Unnamed Skill"}
                      </h3>

                      <p className="mt-1 text-sm text-slate-500">
                        {skill.skillCategory || "Other"}
                      </p>

                      <p className="mt-3 text-sm text-slate-600">
                        Proficiency:{" "}
                        <span className="font-semibold text-slate-900">
                          {skill.proficiencyLevel}
                        </span>
                      </p>

                      <p className="mt-1 text-sm text-slate-600">
                        Experience:{" "}
                        <span className="font-semibold text-slate-900">
                          {skill.yearsOfExperience} years
                        </span>
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            </section>

          </div>
        )}
      </div>
    </main>
  );
}