import { useCallback, useState } from "react";

import type {
  AddUserSkillRequest,
} from "../../types/skill/skillRequest.types";

import type {
  UserSkill,
} from "../../types/skill/skill.types";

import skillService from "../../services/skill/skillService";

export function useAddSkill() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [userSkill, setUserSkill] = useState<UserSkill | null>(null);

  const addSkill = useCallback(
    async (
      request: AddUserSkillRequest,
    ): Promise<UserSkill | null> => {
      setLoading(true);
      setError(null);

      try {
        const data = await skillService.addUserSkill(request);

        setUserSkill(data);

        return data;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Failed to add skill.";

        setError(message);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const reset = useCallback(() => {
    setUserSkill(null);
    setError(null);
    setLoading(false);
  }, []);

  return {
    addSkill,
    userSkill,
    loading,
    error,
    reset,
  };
}

export default useAddSkill;
