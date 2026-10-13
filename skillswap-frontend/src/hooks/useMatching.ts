import { useCallback, useState } from "react";

import type {
  MatchResult,
  MatchSearchRequest,
} from "../types/matching/matching.types";

import matchingService from "../services/matching/matchingService";

export function useMatching() {
  const [matches, setMatches] = useState<MatchResult[]>([]);
  const [selectedMatch, setSelectedMatch] =
    useState<MatchResult | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadMatches = useCallback(
    async (params?: MatchSearchRequest) => {
      setLoading(true);
      setError(null);

      try {
        const data = await matchingService.getMatches(params);

        setMatches(data);

        return data;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Unable to load matches.";

        setError(message);
        return [];
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const loadMatchById = useCallback(
    async (matchId: number) => {
      setLoading(true);
      setError(null);

      try {
        const data =
          await matchingService.getMatchById(matchId);

        setSelectedMatch(data);

        return data;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Unable to load match details.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const clearSelectedMatch = useCallback(() => {
    setSelectedMatch(null);
  }, []);

  return {
    matches,
    selectedMatch,
    loading,
    error,
    loadMatches,
    loadMatchById,
    clearSelectedMatch,
  };
}

export default useMatching;