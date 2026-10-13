import type { UserSkill } from "../../../types/skill/skill.types";

interface SkillDNAProgressProps {
  skills: UserSkill[];
}

const getProficiencyScore = (
  level: UserSkill["proficiencyLevel"],
): number => {
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

const formatLevel = (
  level: UserSkill["proficiencyLevel"],
): string => {
  return level.charAt(0) + level.slice(1).toLowerCase();
};

export default function SkillDNAProgress({
  skills,
}: SkillDNAProgressProps) {
  const activeSkills = skills.filter((skill) => skill.active);

  const orderedSkills = [...activeSkills].sort(
    (a, b) =>
      getProficiencyScore(b.proficiencyLevel) -
      getProficiencyScore(a.proficiencyLevel),
  );

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
          Progress
        </p>

        <h2 className="mt-1 text-xl font-bold text-slate-900">
          Skill Proficiency
        </h2>

        <p className="mt-2 text-sm text-slate-500">
          Your current proficiency level for each active skill.
        </p>
      </div>

      {orderedSkills.length === 0 ? (
        <div className="rounded-xl bg-slate-50 p-8 text-center">
          <p className="text-sm text-slate-500">
            No active skills available.
          </p>
        </div>
      ) : (
        <div className="space-y-5">
          {orderedSkills.map((skill) => {
            const progress = getProficiencyScore(
              skill.proficiencyLevel,
            );

            return (
              <div key={skill.id}>
                <div className="mb-2 flex items-center justify-between gap-4">
                  <div className="min-w-0">
                    <p className="truncate font-medium text-slate-900">
                      {skill.skillName}
                    </p>

                    <p className="text-xs text-slate-500">
                      {formatLevel(skill.proficiencyLevel)}
                    </p>
                  </div>

                  <span className="shrink-0 text-sm font-semibold text-slate-700">
                    {progress}%
                  </span>
                </div>

                <div
                  className="h-2.5 overflow-hidden rounded-full bg-slate-100"
                  aria-label={`${skill.skillName} proficiency ${progress}%`}
                >
                  <div
                    className="h-full rounded-full bg-indigo-600 transition-all duration-500"
                    style={{ width: `${progress}%` }}
                  />
                </div>

                <div className="mt-2 flex flex-wrap gap-3 text-xs text-slate-500">
                  <span>
                    {skill.yearsOfExperience}{" "}
                    {skill.yearsOfExperience === 1
                      ? "year"
                      : "years"}{" "}
                    experience
                  </span>

                  {skill.verified && (
                    <span className="font-medium text-emerald-600">
                      Verified
                    </span>
                  )}

                  {skill.canTeach && (
                    <span className="font-medium text-indigo-600">
                      Can teach
                    </span>
                  )}

                  {skill.wantsToLearn && (
                    <span className="font-medium text-amber-600">
                      Wants to learn
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
}
