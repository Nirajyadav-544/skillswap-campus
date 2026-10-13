
import { useCallback, useEffect, useState } from "react";

import skillService from "../../services/skill/skillService";

import type {
  UserSkill,
  UserSkillSummary,
} from "../../types/skill/skill.types";

import type { UpdateUserSkillRequest } from "../../types/skill/skill.types";

interface UseUserSkillsOptions {
  autoLoad?: boolean;
}

export function useUserSkills(
  options: UseUserSkillsOptions = {}
) {
  const { autoLoad = true } = options;

  const [skills, setSkills] = useState<UserSkill[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // =========================================================
  // LOAD MY SKILLS
  // =========================================================

  const loadSkills = useCallback(async (): Promise<UserSkill[]> => {
    setLoading(true);
    setError(null);

    try {
      const data = await skillService.getMySkills();
      setSkills(data);
      return data;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Failed to load your skills.";

      setError(message);
      setSkills([]);

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // FILTERED SKILLS
  // =========================================================

  const loadActiveSkills = useCallback(
    async (): Promise<UserSkillSummary[]> => {
      setLoading(true);
      setError(null);

      try {
        return await skillService.getMyActiveSkills();
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to load your active skills.";

        setError(message);
        return [];
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const loadTeachingSkills = useCallback(
    async (): Promise<UserSkillSummary[]> => {
      setLoading(true);
      setError(null);

      try {
        return await skillService.getMyTeachingSkills();
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to load your teaching skills.";

        setError(message);
        return [];
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const loadLearningSkills = useCallback(
    async (): Promise<UserSkillSummary[]> => {
      setLoading(true);
      setError(null);

      try {
        return await skillService.getMyLearningSkills();
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to load your learning skills.";

        setError(message);
        return [];
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // =========================================================
  // SINGLE USER SKILL
  // =========================================================

  const getSkillById = useCallback(
    async (userSkillId: number): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        return await skillService.getMySkillById(userSkillId);
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to load your skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const getSkillBySkillId = useCallback(
    async (skillId: number): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        return await skillService.getMySkillBySkillId(skillId);
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to load your skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // =========================================================
  // UPDATE
  // =========================================================

  const updateSkill = useCallback(
    async (
      userSkillId: number,
      data: UpdateUserSkillRequest
    ): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        const updated = await skillService.updateUserSkill(
          userSkillId,
          data
        );

        setSkills((current) =>
          current.map((skill) =>
            skill.id === updated.id ? updated : skill
          )
        );

        return updated;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to update your skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // =========================================================
  // ACTIVATE / DEACTIVATE
  // =========================================================

  const activateSkill = useCallback(
    async (userSkillId: number): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        const updated =
          await skillService.activateUserSkill(userSkillId);

        setSkills((current) =>
          current.map((skill) =>
            skill.id === updated.id ? updated : skill
          )
        );

        return updated;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to activate your skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const deactivateSkill = useCallback(
    async (userSkillId: number): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        const updated =
          await skillService.deactivateUserSkill(userSkillId);

        setSkills((current) =>
          current.map((skill) =>
            skill.id === updated.id ? updated : skill
          )
        );

        return updated;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to deactivate your skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // =========================================================
  // DELETE
  // =========================================================

  const deleteSkill = useCallback(
    async (userSkillId: number): Promise<boolean> => {
      setLoading(true);
      setError(null);

      try {
        await skillService.deleteUserSkill(userSkillId);

        setSkills((current) =>
          current.filter((skill) => skill.id !== userSkillId)
        );

        return true;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to delete your skill.";

        setError(message);
        return false;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // =========================================================
  // REFRESH / ERROR
  // =========================================================

  const refresh = useCallback(async () => {
    await loadSkills();
  }, [loadSkills]);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // =========================================================
  // AUTO LOAD
  // =========================================================

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
    loadActiveSkills,
    loadTeachingSkills,
    loadLearningSkills,

    getSkillById,
    getSkillBySkillId,

    updateSkill,
    activateSkill,
    deactivateSkill,
    deleteSkill,

    refresh,
    clearError,
  };
}

export default useUserSkills;

