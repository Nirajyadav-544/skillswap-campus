import type { Skill } from "../../types/skills/skill.types";
import SkillCard from "./SkillCard";

interface SkillGridProps {
  skills: Skill[];
  loading?: boolean;
  emptyMessage?: string;
  onSelect?: (skill: Skill) => void;
}

const SkillGrid = ({
  skills,
  loading = false,
  emptyMessage = "No skills found.",
  onSelect,
}: SkillGridProps) => {
  if (loading) {
    return (
      <div
        className="
          grid gap-5
          sm:grid-cols-2
          lg:grid-cols-3
          xl:grid-cols-4
        "
      >
        {Array.from({ length: 8 }).map((_, index) => (
          <div
            key={index}
            className="
              animate-pulse rounded-2xl border
              border-slate-200 bg-white p-5
            "
          >
            <div className="mb-4 h-11 w-11 rounded-xl bg-slate-200" />

            <div className="h-5 w-2/3 rounded bg-slate-200" />

            <div className="mt-3 h-4 w-full rounded bg-slate-100" />
            <div className="mt-2 h-4 w-4/5 rounded bg-slate-100" />

            <div className="mt-5 h-10 w-full rounded-xl bg-slate-200" />
          </div>
        ))}
      </div>
    );
  }

  if (skills.length === 0) {
    return (
      <div
        className="
          rounded-2xl border border-dashed
          border-slate-300 bg-white px-6 py-14
          text-center
        "
      >
        <div
          className="
            mx-auto flex h-14 w-14 items-center justify-center
            rounded-2xl bg-slate-100 text-2xl
          "
        >
          ✦
        </div>

        <h3 className="mt-4 text-lg font-bold text-slate-900">
          No skills found
        </h3>

        <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
          {emptyMessage}
        </p>
      </div>
    );
  }

  return (
    <div
      className="
        grid gap-5
        sm:grid-cols-2
        lg:grid-cols-3
        xl:grid-cols-4
      "
    >
      {skills.map((skill) => (
        <SkillCard
          key={skill.id}
          skill={skill}
          onSelect={onSelect}
        />
      ))}
    </div>
  );
};

export default SkillGrid;