import { useCallback, useEffect, useState } from "react";

import type {
  Skill,
  SkillSummary,
} from "../../types/skill/skill.types";

import skillService from "../../services/skill/skillService";

interface UseSkillsOptions {
  autoLoad?: boolean;
  activeOnly?: boolean;
}

export function useSkills(
  options: UseSkillsOptions = {},
) {
  const {
    autoLoad = true,
    activeOnly = true,
  } = options;

  const [skills, setSkills] = useState<Skill[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadSkills = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = activeOnly
        ? await skillService.getActiveSkills()
        : await skillService.getAllSkills();

      setSkills(data);
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Failed to load skills.";

      setError(message);
      setSkills([]);
    } finally {
      setLoading(false);
    }
  }, [activeOnly]);

  const loadPopularSkills = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await skillService.getPopularSkills();
      return data;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Failed to load popular skills.";

      setError(message);
      return [] as SkillSummary[];
    } finally {
      setLoading(false);
    }
  }, []);

  const loadMostUsedSkills = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await skillService.getMostUsedSkills();
      return data;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Failed to load most-used skills.";

      setError(message);
      return [] as SkillSummary[];
    } finally {
      setLoading(false);
    }
  }, []);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  useEffect(() => {
    if (!autoLoad) {
      return;
    }

    void loadSkills();
  }, [autoLoad, loadSkills]);

  return {
    skills,
    loading,
    error,
    loadSkills,
    loadPopularSkills,
    loadMostUsedSkills,
    clearError,
  };
}

export default useSkills;
