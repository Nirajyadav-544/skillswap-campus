import type { ChangeEvent } from "react";
import type { MatchFilters as MatchFiltersType } from "../../types/matching/matching.types";

interface MatchFiltersProps {
  filters: MatchFiltersType;
  onChange: (filters: MatchFiltersType) => void;
  onClear?: () => void;
}

const MatchFilters = ({
  filters,
  onChange,
  onClear,
}: MatchFiltersProps) => {
  const handleSkillChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    onChange({
      ...filters,
      skill: event.target.value || undefined,
    });
  };

  const handleCategoryChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    onChange({
      ...filters,
      category: event.target.value || undefined,
    });
  };

  const handleScoreChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {
    const value = event.target.value;

    onChange({
      ...filters,
      minScore:
        value === "" ? undefined : Number(value),
    });
  };

  const hasFilters =
    Boolean(filters.skill) ||
    Boolean(filters.category) ||
    filters.minScore !== undefined;

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="mb-5 flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-indigo-600">
            Discovery
          </p>

          <h2 className="mt-1 text-lg font-bold text-slate-900">
            Match Filters
          </h2>
        </div>

        {hasFilters && onClear && (
          <button
            type="button"
            onClick={onClear}
            className="rounded-lg px-3 py-2 text-xs font-semibold text-indigo-600 transition hover:bg-indigo-50"
          >
            Clear filters
          </button>
        )}
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <div>
          <label
            htmlFor="match-filter-skill"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Skill
          </label>

          <input
            id="match-filter-skill"
            type="text"
            value={filters.skill ?? ""}
            onChange={handleSkillChange}
            placeholder="e.g. React"
            className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
          />
        </div>

        <div>
          <label
            htmlFor="match-filter-category"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Category
          </label>

          <input
            id="match-filter-category"
            type="text"
            value={filters.category ?? ""}
            onChange={handleCategoryChange}
            placeholder="e.g. Development"
            className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
          />
        </div>

        <div>
          <label
            htmlFor="match-filter-score"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Minimum match score
          </label>

          <input
            id="match-filter-score"
            type="number"
            min="0"
            max="100"
            value={filters.minScore ?? ""}
            onChange={handleScoreChange}
            placeholder="0 - 100"
            className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
          />
        </div>
      </div>
    </section>
  );
};

export default MatchFilters;