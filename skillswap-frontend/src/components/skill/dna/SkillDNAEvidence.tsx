import type { UserSkill } from "../../../types/skill/skill.types";

interface SkillDNAEvidenceProps {
  skills: UserSkill[];
}

interface EvidenceItem {
  label: string;
  value: string;
  description: string;
}

export default function SkillDNAEvidence({
  skills,
}: SkillDNAEvidenceProps) {
  const activeSkills = skills.filter((skill) => skill.active);

  const verifiedCount = activeSkills.filter(
    (skill) => skill.verified,
  ).length;

  const teachingCount = activeSkills.filter(
    (skill) => skill.canTeach,
  ).length;

  const learningCount = activeSkills.filter(
    (skill) => skill.wantsToLearn,
  ).length;

  const experiencedCount = activeSkills.filter(
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

  const totalRatings = activeSkills.reduce(
    (total, skill) => total + skill.ratingCount,
    0,
  );

  const evidence: EvidenceItem[] = [
    {
      label: "Verified Skills",
      value: String(verifiedCount),
      description:
        "Skills marked as verified by the backend.",
    },
    {
      label: "Teaching Skills",
      value: String(teachingCount),
      description:
        "Active skills that are configured for teaching.",
    },
    {
      label: "Learning Skills",
      value: String(learningCount),
      description:
        "Active skills that are configured for learning.",
    },
    {
      label: "Experienced Skills",
      value: String(experiencedCount),
      description:
        "Skills identified as experienced by the backend response.",
    },
    {
      label: "Exchange Activity",
      value: String(totalExchanges),
      description:
        "Total exchange count recorded across your active skills.",
    },
    {
      label: "Successful Exchanges",
      value: String(successfulExchanges),
      description:
        "Successful exchange count recorded across your active skills.",
    },
    {
      label: "Ratings Received",
      value: String(totalRatings),
      description:
        "Total rating count recorded across your active skills.",
    },
  ];

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
          Evidence
        </p>

        <h2 className="mt-1 text-xl font-bold text-slate-900">
          Skill DNA Evidence
        </h2>

        <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
          These indicators are based on data already returned by the
          SkillSwap Campus user-skill API. No separate Skill DNA
          backend endpoint is assumed here.
        </p>
      </div>

      {activeSkills.length === 0 ? (
        <div className="rounded-xl bg-slate-50 p-8 text-center">
          <p className="text-sm text-slate-500">
            Add active skills to start building your evidence profile.
          </p>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {evidence.map((item) => (
            <article
              key={item.label}
              className="rounded-xl border border-slate-200 p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-medium text-slate-500">
                    {item.label}
                  </p>

                  <p className="mt-1 text-2xl font-bold text-slate-900">
                    {item.value}
                  </p>
                </div>

                <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-indigo-50 text-sm font-bold text-indigo-600">
                  ?
                </div>
              </div>

              <p className="mt-3 text-xs leading-5 text-slate-500">
                {item.description}
              </p>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
