import type { UserSkill } from "../../../types/skill/skill.types";

interface SkillDNACategoryProps {
  skills: UserSkill[];
}

interface CategoryData {
  name: string;
  total: number;
  teaching: number;
  learning: number;
  verified: number;
}

const formatCategory = (category: string | null): string => {
  if (!category) {
    return "Uncategorized";
  }

  return category
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
};

export default function SkillDNACategory({
  skills,
}: SkillDNACategoryProps) {
  const categoryMap = new Map<string, CategoryData>();

  skills
    .filter((skill) => skill.active)
    .forEach((skill) => {
      const key = skill.skillCategory ?? "OTHER";
      const existing = categoryMap.get(key);

      if (existing) {
        existing.total += 1;

        if (skill.canTeach) {
          existing.teaching += 1;
        }

        if (skill.wantsToLearn) {
          existing.learning += 1;
        }

        if (skill.verified) {
          existing.verified += 1;
        }

        return;
      }

      categoryMap.set(key, {
        name: formatCategory(skill.skillCategory),
        total: 1,
        teaching: skill.canTeach ? 1 : 0,
        learning: skill.wantsToLearn ? 1 : 0,
        verified: skill.verified ? 1 : 0,
      });
    });

  const categories = Array.from(categoryMap.values()).sort(
    (a, b) => b.total - a.total,
  );

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
          Skill Distribution
        </p>

        <h2 className="mt-1 text-xl font-bold text-slate-900">
          Skills by Category
        </h2>

        <p className="mt-2 text-sm text-slate-500">
          Distribution of your active skills across the backend-defined
          skill categories.
        </p>
      </div>

      {categories.length === 0 ? (
        <div className="rounded-xl bg-slate-50 p-8 text-center">
          <p className="text-sm text-slate-500">
            Add an active skill to build your category profile.
          </p>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {categories.map((category) => (
            <div
              key={category.name}
              className="rounded-xl border border-slate-200 p-4"
            >
              <div className="flex items-center justify-between gap-4">
                <h3 className="font-semibold text-slate-900">
                  {category.name}
                </h3>

                <span className="rounded-full bg-indigo-50 px-3 py-1 text-xs font-semibold text-indigo-700">
                  {category.total}{" "}
                  {category.total === 1 ? "skill" : "skills"}
                </span>
              </div>

              <div className="mt-4 grid grid-cols-3 gap-2 text-center">
                <div className="rounded-lg bg-slate-50 p-3">
                  <p className="text-lg font-bold text-slate-900">
                    {category.teaching}
                  </p>
                  <p className="text-xs text-slate-500">Teach</p>
                </div>

                <div className="rounded-lg bg-slate-50 p-3">
                  <p className="text-lg font-bold text-slate-900">
                    {category.learning}
                  </p>
                  <p className="text-xs text-slate-500">Learn</p>
                </div>

                <div className="rounded-lg bg-slate-50 p-3">
                  <p className="text-lg font-bold text-slate-900">
                    {category.verified}
                  </p>
                  <p className="text-xs text-slate-500">Verified</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
