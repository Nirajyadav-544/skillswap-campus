import { useCallback, useState } from "react";

import type {
  SkillSummary,
} from "../../types/skill/skill.types";

import skillService from "../../services/skill/skillService";

interface UseSkillSearchOptions {
  debounceMs?: number;
}

export function useSkillSearch(
  options: UseSkillSearchOptions = {},
) {
  const {
    debounceMs = 300,
  } = options;

  const [results, setResults] = useState<SkillSummary[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState("");

  const search = useCallback(
    async (searchQuery: string) => {
      const normalizedQuery = searchQuery.trim();

      setQuery(searchQuery);
      setError(null);

      if (!normalizedQuery) {
        setResults([]);
        setLoading(false);
        return [];
      }

      setLoading(true);

      try {
        const data = await skillService.searchSkills(
          normalizedQuery,
        );

        setResults(data);

        return data;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to search skills.";

        setError(message);
        setResults([]);

        return [];
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const clearSearch = useCallback(() => {
    setQuery("");
    setResults([]);
    setError(null);
    setLoading(false);
  }, []);

  const searchWithDebounce = useCallback(
    async (searchQuery: string) => {
      const normalizedQuery = searchQuery.trim();

      setQuery(searchQuery);
      setError(null);

      if (!normalizedQuery) {
        setResults([]);
        setLoading(false);
        return [];
      }

      setLoading(true);

      await new Promise<void>((resolve) => {
        window.setTimeout(resolve, debounceMs);
      });

      try {
        const data = await skillService.searchSkills(
          normalizedQuery,
        );

        setResults(data);

        return data;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to search skills.";

        setError(message);
        setResults([]);

        return [];
      } finally {
        setLoading(false);
      }
    },
    [debounceMs],
  );

  return {
    query,
    results,
    loading,
    error,
    search,
    searchWithDebounce,
    clearSearch,
  };
}

export default useSkillSearch;
