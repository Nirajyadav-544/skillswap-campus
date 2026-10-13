import { useMemo, useState } from "react";

import MatchCard from "../../components/matching/MatchCard";
import type {
  MatchFilters,
  MatchResult,
} from "../../types/matching/matching.types";

const MatchingPage = () => {
  const [matches] = useState<MatchResult[]>([]);
  const [filters, setFilters] = useState<MatchFilters>({});

  const filteredMatches = useMemo(() => {
    const skill = filters.skill?.trim().toLowerCase();

    return matches.filter((match) => {
      const matchesSkill =
        !skill ||
        match.skillName?.toLowerCase().includes(skill);

      const matchesScore =
        filters.minScore === undefined ||
        (match.matchScore ?? 0) >= filters.minScore;

      return matchesSkill && matchesScore;
    });
  }, [matches, filters]);

  const clearFilters = () => {
    setFilters({});
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-7xl">
        <div className="mb-8">
          <p className="text-sm font-semibold text-indigo-600">
            SkillSwap Campus
          </p>

          <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">
            Skill Matching
          </h1>

          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
            Discover students whose skills may align with your
            learning, collaboration, and exchange goals.
          </p>
        </div>

        <section className="mb-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-end">
            <div className="flex-1">
              <label
                htmlFor="match-skill"
                className="mb-2 block text-sm font-semibold text-slate-700"
              >
                Skill
              </label>

              <input
                id="match-skill"
                type="text"
                value={filters.skill ?? ""}
                onChange={(event) =>
                  setFilters((current) => ({
                    ...current,
                    skill: event.target.value,
                  }))
                }
                placeholder="Search by skill..."
                className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
              />
            </div>

            <div className="w-full lg:w-48">
              <label
                htmlFor="match-score"
                className="mb-2 block text-sm font-semibold text-slate-700"
              >
                Minimum score
              </label>

              <input
                id="match-score"
                type="number"
                min="0"
                max="100"
                value={filters.minScore ?? ""}
                onChange={(event) => {
                  const value = event.target.value;

                  setFilters((current) => ({
                    ...current,
                    minScore:
                      value === "" ? undefined : Number(value),
                  }));
                }}
                placeholder="0 - 100"
                className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
              />
            </div>

            {(filters.skill || filters.minScore !== undefined) && (
              <button
                type="button"
                onClick={clearFilters}
                className="h-11 rounded-xl border border-slate-200 px-5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 hover:text-indigo-600"
              >
                Clear
              </button>
            )}
          </div>
        </section>

        <div className="mb-5 flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-900">
              Potential Matches
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              {filteredMatches.length}{" "}
              {filteredMatches.length === 1
                ? "match"
                : "matches"}{" "}
              available
            </p>
          </div>
        </div>

        {filteredMatches.length === 0 ? (
          <section className="rounded-3xl border border-dashed border-slate-300 bg-white px-6 py-16 text-center shadow-sm">
            <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-50 text-2xl text-indigo-600">
              ✦
            </div>

            <h2 className="mt-5 text-xl font-bold text-slate-900">
              No matches available yet
            </h2>

            <p className="mx-auto mt-2 max-w-lg text-sm leading-6 text-slate-500">
              Matching results will appear here once the matching
              engine provides compatible skill profiles.
            </p>
          </section>
        ) : (
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {filteredMatches.map((match) => (
              <MatchCard
                key={match.id}
                match={match}
              />
            ))}
          </div>
        )}
      </div>
    </main>
  );
};

export default MatchingPage;