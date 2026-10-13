import type { MatchResult } from "../../types/matching/matching.types";

interface MatchCardProps {
  match: MatchResult;
  onSelect?: (match: MatchResult) => void;
}

const MatchCard = ({
  match,
  onSelect,
}: MatchCardProps) => {
  const score =
    typeof match.matchScore === "number"
      ? Math.round(match.matchScore)
      : null;

  const initials =
    match.matchedUserName
      ?.split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0))
      .join("")
      .toUpperCase() || "U";

  return (
    <article className="group rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-indigo-200 hover:shadow-lg">
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 text-sm font-bold text-white">
            {initials}
          </div>

          <div className="min-w-0">
            <h3 className="truncate font-bold text-slate-900">
              {match.matchedUserName || "Campus Student"}
            </h3>

            <p className="mt-0.5 text-xs text-slate-400">
              User #{match.matchedUserId}
            </p>
          </div>
        </div>

        {score !== null && (
          <div className="shrink-0 text-right">
            <p className="text-2xl font-bold text-indigo-600">
              {score}%
            </p>

            <p className="text-[11px] font-semibold uppercase tracking-wide text-slate-400">
              Match
            </p>
          </div>
        )}
      </div>

      {match.skillName && (
        <div className="mt-5 rounded-xl bg-indigo-50 p-3">
          <p className="text-xs font-semibold uppercase tracking-wide text-indigo-400">
            Skill
          </p>

          <p className="mt-1 text-sm font-bold text-indigo-700">
            {match.skillName}
          </p>
        </div>
      )}

      {match.matchReason && (
        <div className="mt-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
            Why this match
          </p>

          <p className="mt-1 line-clamp-3 text-sm leading-6 text-slate-600">
            {match.matchReason}
          </p>
        </div>
      )}

      {onSelect && (
        <button
          type="button"
          onClick={() => onSelect(match)}
          className="mt-5 w-full rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
        >
          View Match
        </button>
      )}
    </article>
  );
};

export default MatchCard;