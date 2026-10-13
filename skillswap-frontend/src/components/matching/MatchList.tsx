import type { MatchResult } from "../../types/matching/matching.types";
import MatchCard from "./MatchCard";

interface MatchListProps {
  matches: MatchResult[];
  loading?: boolean;
  emptyMessage?: string;
  onSelect?: (match: MatchResult) => void;
}

const MatchList = ({
  matches,
  loading = false,
  emptyMessage = "No matching profiles found.",
  onSelect,
}: MatchListProps) => {
  if (loading) {
    return (
      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, index) => (
          <div
            key={index}
            className="animate-pulse rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
          >
            <div className="flex items-center gap-3">
              <div className="h-12 w-12 rounded-2xl bg-slate-200" />
              <div className="flex-1">
                <div className="h-4 w-32 rounded bg-slate-200" />
                <div className="mt-2 h-3 w-20 rounded bg-slate-100" />
              </div>
            </div>

            <div className="mt-5 h-14 rounded-xl bg-slate-100" />
            <div className="mt-4 h-4 w-full rounded bg-slate-100" />
            <div className="mt-2 h-4 w-4/5 rounded bg-slate-100" />
          </div>
        ))}
      </div>
    );
  }

  if (matches.length === 0) {
    return (
      <div className="rounded-3xl border border-dashed border-slate-300 bg-white px-6 py-14 text-center shadow-sm">
        <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-50 text-xl text-indigo-600">
          ✦
        </div>

        <h3 className="mt-4 text-lg font-bold text-slate-900">
          No matches found
        </h3>

        <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
          {emptyMessage}
        </p>
      </div>
    );
  }

  return (
    <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
      {matches.map((match) => (
        <MatchCard
          key={match.id}
          match={match}
          onSelect={onSelect}
        />
      ))}
    </div>
  );
};

export default MatchList;