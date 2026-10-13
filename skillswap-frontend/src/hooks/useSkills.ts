import { useCallback, useState } from "react";

import skillService from "../services/skill/skillService";

import type {
AddUserSkillRequest,
ApiPage,
Skill,
SkillSummary,
UserSkill,
UserSkillSummary,
UpdateUserSkillRequest,
} from "../types/skill/skill.types";

import type { SkillCategory } from "../types/skill/skill.enums";

export function useSkills() {
const [skills, setSkills] = useState<Skill[]>([]);
const [mySkills, setMySkills] = useState<UserSkill[]>([]);
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);

const loadSkills = useCallback(async (): Promise<Skill[]> => {
setLoading(true);
setError(null);


try {
  const data = await skillService.getAllSkills();
  setSkills(data);
  return data;
} catch (err) {
  const message =
    err instanceof Error ? err.message : "Unable to load skills.";

  setError(message);
  return [];
} finally {
  setLoading(false);
}


}, []);

const loadActiveSkills = useCallback(async (): Promise<Skill[]> => {
setLoading(true);
setError(null);


try {
  const data = await skillService.getActiveSkills();
  setSkills(data);
  return data;
} catch (err) {
  const message =
    err instanceof Error
      ? err.message
      : "Unable to load active skills.";

  setError(message);
  return [];
} finally {
  setLoading(false);
}


}, []);

const searchSkills = useCallback(
async (query = ""): Promise<SkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.searchSkills(query);
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to search skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const searchSkillsPaginated = useCallback(
async (
query = "",
page = 0,
size = 20,
): Promise<ApiPage<SkillSummary> | null> => {
setLoading(true);
setError(null);


  try {
    return await skillService.searchSkillsPaginated(
      query,
      page,
      size,
    );
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to search skills.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const loadPopularSkills = useCallback(
async (): Promise<SkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getPopularSkills();
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load popular skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const loadMostUsedSkills = useCallback(
async (): Promise<SkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMostUsedSkills();
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load most used skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const loadSkillsByCategory = useCallback(
async (category: SkillCategory): Promise<Skill[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getSkillsByCategory(category);
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load skills by category.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const getSkillById = useCallback(
async (skillId: number): Promise<Skill | null> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getSkillById(skillId);
  } catch (err) {
    const message =
      err instanceof Error ? err.message : "Unable to load skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const loadMySkills = useCallback(async (): Promise<UserSkill[]> => {
setLoading(true);
setError(null);


try {
  const data = await skillService.getMySkills();
  setMySkills(data);
  return data;
} catch (err) {
  const message =
    err instanceof Error
      ? err.message
      : "Unable to load your skills.";

  setError(message);
  return [];
} finally {
  setLoading(false);
}


}, []);

const loadMySkillsPage = useCallback(
async (
page = 0,
size = 20,
): Promise<ApiPage<UserSkillSummary> | null> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMySkillsPage(page, size);
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load your skills.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const loadMyActiveSkills = useCallback(
async (): Promise<UserSkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMyActiveSkills();
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load active skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const loadMyTeachingSkills = useCallback(
async (): Promise<UserSkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMyTeachingSkills();
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load teaching skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const loadMyLearningSkills = useCallback(
async (): Promise<UserSkillSummary[]> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMyLearningSkills();
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load learning skills.";

    setError(message);
    return [];
  } finally {
    setLoading(false);
  }
},
[],


);

const getMySkillById = useCallback(
async (userSkillId: number): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMySkillById(userSkillId);
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load your skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const getMySkillBySkillId = useCallback(
async (skillId: number): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    return await skillService.getMySkillBySkillId(skillId);
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to load your skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const addMySkill = useCallback(
async (
data: AddUserSkillRequest,
): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    const created = await skillService.addUserSkill(data);

    setMySkills((current) => [
      ...current.filter((skill) => skill.id !== created.id),
      created,
    ]);

    return created;
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to add skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const updateMySkill = useCallback(
async (
userSkillId: number,
data: UpdateUserSkillRequest,
): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    const updated = await skillService.updateUserSkill(
      userSkillId,
      data,
    );

    setMySkills((current) =>
      current.map((skill) =>
        skill.id === updated.id ? updated : skill,
      ),
    );

    return updated;
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to update skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const deleteMySkill = useCallback(
async (userSkillId: number): Promise<boolean> => {
setLoading(true);
setError(null);


  try {
    await skillService.deleteUserSkill(userSkillId);

    setMySkills((current) =>
      current.filter((skill) => skill.id !== userSkillId),
    );

    return true;
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to delete skill.";

    setError(message);
    return false;
  } finally {
    setLoading(false);
  }
},
[],


);

const activateMySkill = useCallback(
async (userSkillId: number): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    const updated =
      await skillService.activateUserSkill(userSkillId);

    setMySkills((current) =>
      current.map((skill) =>
        skill.id === updated.id ? updated : skill,
      ),
    );

    return updated;
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to activate skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const deactivateMySkill = useCallback(
async (userSkillId: number): Promise<UserSkill | null> => {
setLoading(true);
setError(null);


  try {
    const updated =
      await skillService.deactivateUserSkill(userSkillId);

    setMySkills((current) =>
      current.map((skill) =>
        skill.id === updated.id ? updated : skill,
      ),
    );

    return updated;
  } catch (err) {
    const message =
      err instanceof Error
        ? err.message
        : "Unable to deactivate skill.";

    setError(message);
    return null;
  } finally {
    setLoading(false);
  }
},
[],


);

const clearError = useCallback(() => {
setError(null);
}, []);

return {
skills,
mySkills,
loading,
error,


loadSkills,
loadActiveSkills,
searchSkills,
searchSkillsPaginated,
loadPopularSkills,
loadMostUsedSkills,
loadSkillsByCategory,
getSkillById,

loadMySkills,
loadMySkillsPage,
loadMyActiveSkills,
loadMyTeachingSkills,
loadMyLearningSkills,
getMySkillById,
getMySkillBySkillId,
addMySkill,
updateMySkill,
deleteMySkill,
activateMySkill,
deactivateMySkill,

clearError,

};
}

export default useSkills;
