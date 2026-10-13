import type { MatchResult } from "../../types/matching/matching.types";

interface MatchDetailsProps {
  match: MatchResult;
  onClose?: () => void;
  onAction?: (match: MatchResult) => void;
  actionLabel?: string;
}

const MatchDetails = ({
  match,
  onClose,
  onAction,
  actionLabel = "Start Exchange",
}: MatchDetailsProps) => {
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
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8">
      <div className="flex items-start justify-between gap-5">
        <div className="flex items-center gap-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 text-lg font-bold text-white shadow-sm">
            {initials}
          </div>

          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-indigo-600">
              Match profile
            </p>

            <h2 className="mt-1 text-2xl font-bold text-slate-900">
              {match.matchedUserName || "Campus Student"}
            </h2>

            <p className="mt-1 text-sm text-slate-400">
              User #{match.matchedUserId}
            </p>
          </div>
        </div>

        {onClose && (
          <button
            type="button"
            onClick={onClose}
            aria-label="Close match details"
            className="rounded-xl px-3 py-2 text-xl text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
          >
            ×
          </button>
        )}
      </div>

      <div className="mt-7 grid gap-4 sm:grid-cols-2">
        <div className="rounded-2xl bg-indigo-50 p-5">
          <p className="text-xs font-semibold uppercase tracking-wide text-indigo-400">
            Match score
          </p>

          <div className="mt-2 flex items-end gap-1">
            <span className="text-4xl font-bold text-indigo-700">
              {score !== null ? score : "—"}
            </span>

            {score !== null && (
              <span className="mb-1 text-sm font-semibold text-indigo-500">
                %
              </span>
            )}
          </div>
        </div>

        <div className="rounded-2xl bg-slate-50 p-5">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
            Matched skill
          </p>

          <p className="mt-2 text-lg font-bold text-slate-800">
            {match.skillName || "Not specified"}
          </p>
        </div>
      </div>

      <div className="mt-6">
        <p className="text-xs font-bold uppercase tracking-[0.15em] text-slate-400">
          Match explanation
        </p>

        <div className="mt-3 rounded-2xl border border-slate-200 bg-slate-50 p-5">
          <p className="text-sm leading-7 text-slate-600">
            {match.matchReason ||
              "No additional match explanation is available yet."}
          </p>
        </div>
      </div>

      {(onAction || onClose) && (
        <div className="mt-7 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          {onClose && (
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-slate-200 px-5 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              Close
            </button>
          )}

          {onAction && (
            <button
              type="button"
              onClick={() => onAction(match)}
              className="rounded-xl bg-indigo-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              {actionLabel}
            </button>
          )}
        </div>
      )}
    </section>
  );
};

export default MatchDetails;