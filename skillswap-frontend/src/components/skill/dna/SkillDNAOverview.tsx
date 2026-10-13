import type { UserSkill } from "../../../types/skill/skill.types";

interface SkillDNAOverviewProps {
  skills: UserSkill[];
  loading?: boolean;
}

const getLevelScore = (level: UserSkill["proficiencyLevel"]): number => {
  switch (level) {
    case "BEGINNER":
      return 25;
    case "INTERMEDIATE":
      return 50;
    case "ADVANCED":
      return 75;
    case "EXPERT":
      return 100;
    default:
      return 0;
  }
};

const formatLevel = (level: UserSkill["proficiencyLevel"]): string => {
  return level.charAt(0) + level.slice(1).toLowerCase();
};

export default function SkillDNAOverview({
  skills,
  loading = false,
}: SkillDNAOverviewProps) {
  if (loading) {
    return (
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="animate-pulse space-y-4">
          <div className="h-6 w-48 rounded bg-slate-200" />
          <div className="h-4 w-72 rounded bg-slate-200" />
          <div className="grid gap-4 sm:grid-cols-3">
            <div className="h-24 rounded-xl bg-slate-100" />
            <div className="h-24 rounded-xl bg-slate-100" />
            <div className="h-24 rounded-xl bg-slate-100" />
          </div>
        </div>
      </section>
    );
  }

  const activeSkills = skills.filter((skill) => skill.active);
  const teachingSkills = activeSkills.filter((skill) => skill.canTeach);
  const learningSkills = activeSkills.filter(
    (skill) => skill.wantsToLearn,
  );
  const verifiedSkills = activeSkills.filter(
    (skill) => skill.verified,
  );

  const averageProficiency =
    activeSkills.length > 0
      ? Math.round(
          activeSkills.reduce(
            (total, skill) =>
              total + getLevelScore(skill.proficiencyLevel),
            0,
          ) / activeSkills.length,
        )
      : 0;

  const experiencedSkills = activeSkills.filter(
    (skill) => skill.experienced,
  ).length;

  const totalExchanges = activeSkills.reduce(
    (total, skill) => total + skill.exchangeCount,
    0,
  );

  const successfulExchanges = activeSkills.reduce(
    (total, skill) => total + skill.successfulExchangeCount,
    0,
  );

  const successRate =
    totalExchanges > 0
      ? Math.round((successfulExchanges / totalExchanges) * 100)
      : 0;

  const strongestSkills = [...activeSkills]
    .sort(
      (a, b) =>
        getLevelScore(b.proficiencyLevel) -
        getLevelScore(a.proficiencyLevel),
    )
    .slice(0, 5);

  return (
    <section className="space-y-6">
      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-6">
          <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
            Skill DNA
          </p>

          <h2 className="mt-1 text-2xl font-bold text-slate-900">
            Your Skill Profile
          </h2>

          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
            Your Skill DNA is currently derived from your registered
            skills, proficiency levels, experience, verification and
            exchange activity.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Active Skills</p>
            <p className="mt-1 text-2xl font-bold text-slate-900">
              {activeSkills.length}
            </p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Can Teach</p>
            <p className="mt-1 text-2xl font-bold text-slate-900">
              {teachingSkills.length}
            </p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Learning</p>
            <p className="mt-1 text-2xl font-bold text-slate-900">
              {learningSkills.length}
            </p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Verified</p>
            <p className="mt-1 text-2xl font-bold text-slate-900">
              {verifiedSkills.length}
            </p>
          </div>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm font-medium text-slate-500">
            Proficiency Index
          </p>

          <div className="mt-3 flex items-end gap-2">
            <span className="text-3xl font-bold text-slate-900">
              {averageProficiency}%
            </span>
          </div>

          <div className="mt-3 h-2 overflow-hidden rounded-full bg-slate-100">
            <div
              className="h-full rounded-full bg-indigo-600 transition-all"
              style={{ width: `${averageProficiency}%` }}
            />
          </div>
        </div>

        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm font-medium text-slate-500">
            Experienced Skills
          </p>

          <p className="mt-3 text-3xl font-bold text-slate-900">
            {experiencedSkills}
          </p>

          <p className="mt-1 text-sm text-slate-500">
            Based on your registered experience.
          </p>
        </div>

        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm font-medium text-slate-500">
            Exchange Success
          </p>

          <p className="mt-3 text-3xl font-bold text-slate-900">
            {successRate}%
          </p>

          <p className="mt-1 text-sm text-slate-500">
            {successfulExchanges} successful of {totalExchanges} exchanges
          </p>
        </div>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-5">
          <h3 className="text-lg font-bold text-slate-900">
            Strongest Skills
          </h3>

          <p className="mt-1 text-sm text-slate-500">
            Skills with the highest registered proficiency.
          </p>
        </div>

        {strongestSkills.length === 0 ? (
          <p className="rounded-xl bg-slate-50 p-5 text-center text-sm text-slate-500">
            No active skills have been added yet.
          </p>
        ) : (
          <div className="space-y-4">
            {strongestSkills.map((skill) => {
              const score = getLevelScore(skill.proficiencyLevel);

              return (
                <div key={skill.id}>
                  <div className="mb-2 flex items-center justify-between gap-4">
                    <span className="font-medium text-slate-800">
                      {skill.skillName}
                    </span>

                    <span className="text-sm font-medium text-slate-500">
                      {formatLevel(skill.proficiencyLevel)}
                    </span>
                  </div>

                  <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                    <div
                      className="h-full rounded-full bg-indigo-600"
                      style={{ width: `${score}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
}
