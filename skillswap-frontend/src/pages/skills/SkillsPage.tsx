import { useEffect, useMemo, useState } from "react";

import SkillList from "../../components/skill/SkillList";
import SkillSearch from "../../components/skill/SkillSearch";

import useSkills from "../../hooks/skill/useSkills";

import type {
Skill,
SkillSummary,
} from "../../types/skill/skill.types";

type SkillListItem = Skill | SkillSummary;

export default function SkillsPage() {
const {
skills,
loading,
error,
loadSkills,
clearError,
} = useSkills();

const [searchQuery, setSearchQuery] = useState("");
const [searchResults, setSearchResults] = useState<
SkillSummary[]

> ([]);

useEffect(() => {
void loadSkills();
}, [loadSkills]);

const displayedSkills = useMemo<SkillListItem[]>(() => {
if (searchQuery.trim()) {
return searchResults;
}


return skills;


}, [
searchQuery,
searchResults,
skills,
]);

const handleSearchChange = (
query: string,
) => {
setSearchQuery(query);


if (!query.trim()) {
  setSearchResults([]);
  clearError();
}


};

const handleSearchResults = (
results: SkillSummary[],
) => {
if (!searchQuery.trim()) {
setSearchResults([]);
return;
}


setSearchResults(results);


};

const handleSearchSelect = (
skill: SkillSummary,
) => {
setSearchQuery(skill.name);
setSearchResults([skill]);
};

const handleClearSearch = () => {
setSearchQuery("");
setSearchResults([]);
clearError();
};

const displayError = error;

const displayLoading =
loading && !searchQuery.trim();

return ( <main className="min-h-screen bg-slate-50"> <div className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8"> <section className="mb-8"> <div className="mb-6"> <p className="mb-2 text-sm font-semibold uppercase tracking-wide text-indigo-600">
SkillSwap Campus </p>

```
        <h1 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
          Explore Skills
        </h1>

        <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600 sm:text-base">
          Discover skills available across the SkillSwap
          Campus ecosystem and find skills you want to
          learn or exchange.
        </p>
      </div>

      <div className="max-w-2xl">
        <SkillSearch
          value={searchQuery}
          onChange={handleSearchChange}
          onSelect={handleSearchSelect}
          onResults={handleSearchResults}
        />
      </div>
    </section>

    <section>
      <div className="mb-5 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="text-xl font-semibold text-slate-900">
            {searchQuery.trim()
              ? "Search Results"
              : "All Skills"}
          </h2>

          {!displayLoading &&
            !displayError && (
              <p className="mt-1 text-sm text-slate-500">
                {displayedSkills.length}{" "}
                {displayedSkills.length === 1
                  ? "skill"
                  : "skills"}{" "}
                {searchQuery.trim()
                  ? "found"
                  : "available"}
              </p>
            )}
        </div>

        {searchQuery.trim() && (
          <button
            type="button"
            onClick={handleClearSearch}
            className="w-fit rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
          >
            Clear Search
          </button>
        )}
      </div>

      <SkillList
        skills={displayedSkills}
        loading={displayLoading}
        error={displayError}
        emptyMessage={
          searchQuery.trim()
            ? `No skills found for "${searchQuery.trim()}".`
            : "No skills are available yet."
        }
        showDescription
        showPopularity
        showUserCount
      />
    </section>
  </div>
</main>


);
}
