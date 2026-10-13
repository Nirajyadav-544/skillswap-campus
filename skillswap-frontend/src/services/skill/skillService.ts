import apiClient from "../api/apiClient";
import storageService from "../../utils/storage";

import type {
  ApiPage,
  Skill,
  SkillSummary,
  UserSkill,
  UserSkillSummary,
} from "../../types/skill/skill.types";

import type {
  AddUserSkillRequest,
  CreateSkillRequest,
  UpdateSkillRequest,
  UpdateUserSkillRequest,
} from "../../types/skill/skillRequest.types";

import type { SkillCategory } from "../../types/skill/skill.enums";

const getToken = (): string => {
  const token = storageService.getAccessToken();

  if (!token) {
    throw new Error(
      "Authentication token is missing. Please login again.",
    );
  }

  return token;
};

const skillService = {
  // ==========================================================
  // SKILL CATALOG
  // ==========================================================

  async createSkill(
    data: CreateSkillRequest,
  ): Promise<Skill> {
    return apiClient.post<Skill>(
      "/v1/skills",
      data,
      getToken(),
    );
  },

  async createSystemSkill(
    data: CreateSkillRequest,
  ): Promise<Skill> {
    return apiClient.post<Skill>(
      "/v1/skills/system",
      data,
      getToken(),
    );
  },

  async getAllSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      "/v1/skills",
      getToken(),
    );
  },

  async getActiveSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      "/v1/skills/active",
      getToken(),
    );
  },

  async getPopularSkills(): Promise<SkillSummary[]> {
    return apiClient.get<SkillSummary[]>(
      "/v1/skills/popular",
      getToken(),
    );
  },

  async getMostUsedSkills(): Promise<SkillSummary[]> {
    return apiClient.get<SkillSummary[]>(
      "/v1/skills/most-used",
      getToken(),
    );
  },

  async getSystemDefinedSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      "/v1/skills/system",
      getToken(),
    );
  },

  async getCustomSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      "/v1/skills/custom",
      getToken(),
    );
  },

  async searchSkills(
    query: string,
  ): Promise<SkillSummary[]> {
    const params = new URLSearchParams({
      q: query,
    });

    return apiClient.get<SkillSummary[]>(
      `/v1/skills/search?${params.toString()}`,
      getToken(),
    );
  },

  async searchSkillsPaginated(
    query: string,
    page = 0,
    size = 20,
  ): Promise<ApiPage<SkillSummary>> {
    const params = new URLSearchParams({
      q: query,
      page: String(page),
      size: String(size),
    });

    return apiClient.get<ApiPage<SkillSummary>>(
      `/v1/skills/search/page?${params.toString()}`,
      getToken(),
    );
  },

  async getSkillsByCategory(
    category: SkillCategory,
  ): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      `/v1/skills/category/${encodeURIComponent(category)}`,
      getToken(),
    );
  },

  async getTeachersForSkill(
  skillId: number,
): Promise<UserSkillSummary[]> {
  return apiClient.get<UserSkillSummary[]>(
    `/v1/skill-discovery/skills/${skillId}/teachers`,
    getToken(),
  );
},


  async getSkillById(
    skillId: number,
  ): Promise<Skill> {
    return apiClient.get<Skill>(
      `/v1/skills/${skillId}`,
      getToken(),
    );
  },

  async getSkillByName(
    name: string,
  ): Promise<Skill> {
    return apiClient.get<Skill>(
      `/v1/skills/name/${encodeURIComponent(name)}`,
      getToken(),
    );
  },

  async updateSkill(
    skillId: number,
    data: UpdateSkillRequest,
  ): Promise<Skill> {
    return apiClient.put<Skill>(
      `/v1/skills/${skillId}`,
      data,
      getToken(),
    );
  },

  async activateSkill(
    skillId: number,
  ): Promise<Skill> {
    return apiClient.patch<Skill>(
      `/v1/skills/${skillId}/activate`,
      undefined,
      getToken(),
    );
  },

  async deactivateSkill(
    skillId: number,
  ): Promise<Skill> {
    return apiClient.patch<Skill>(
      `/v1/skills/${skillId}/deactivate`,
      undefined,
      getToken(),
    );
  },

  async deleteSkill(
    skillId: number,
  ): Promise<void> {
    await apiClient.delete<void>(
      `/v1/skills/${skillId}`,
      getToken(),
    );
  },

  // ==========================================================
  // CURRENT USER'S SKILLS
  // ==========================================================

  async addUserSkill(
    data: AddUserSkillRequest,
  ): Promise<UserSkill> {
    return apiClient.post<UserSkill>(
      "/v1/users/me/skills",
      data,
      getToken(),
    );
  },

  async getMySkills(): Promise<UserSkill[]> {
    return apiClient.get<UserSkill[]>(
      "/v1/users/me/skills",
      getToken(),
    );
  },

  async getMySkillsPage(
    page = 0,
    size = 20,
  ): Promise<ApiPage<UserSkillSummary>> {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
    });

    return apiClient.get<ApiPage<UserSkillSummary>>(
      `/v1/users/me/skills/page?${params.toString()}`,
      getToken(),
    );
  },

  async getMyActiveSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/v1/users/me/skills/active",
      getToken(),
    );
  },

  async getMyTeachingSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/v1/users/me/skills/teaching",
      getToken(),
    );
  },

  async getMyLearningSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/v1/users/me/skills/learning",
      getToken(),
    );
  },

  async getMySkillById(
    userSkillId: number,
  ): Promise<UserSkill> {
    return apiClient.get<UserSkill>(
      `/v1/users/me/skills/${userSkillId}`,
      getToken(),
    );
  },

  async getMySkillBySkillId(
    skillId: number,
  ): Promise<UserSkill> {
    return apiClient.get<UserSkill>(
      `/v1/users/me/skills/skill/${skillId}`,
      getToken(),
    );
  },

  async updateUserSkill(
    userSkillId: number,
    data: UpdateUserSkillRequest,
  ): Promise<UserSkill> {
    return apiClient.put<UserSkill>(
      `/v1/users/me/skills/${userSkillId}`,
      data,
      getToken(),
    );
  },

  async activateUserSkill(
    userSkillId: number,
  ): Promise<UserSkill> {
    return apiClient.patch<UserSkill>(
      `/v1/users/me/skills/${userSkillId}/activate`,
      undefined,
      getToken(),
    );
  },

  async deactivateUserSkill(
    userSkillId: number,
  ): Promise<UserSkill> {
    return apiClient.patch<UserSkill>(
      `/v1/users/me/skills/${userSkillId}/deactivate`,
      undefined,
      getToken(),
    );
  },

  async deleteUserSkill(
    userSkillId: number,
  ): Promise<void> {
    await apiClient.delete<void>(
      `/v1/users/me/skills/${userSkillId}`,
      getToken(),
    );
  },

  async deleteUserSkillBySkillId(
    skillId: number,
  ): Promise<void> {
    await apiClient.delete<void>(
      `/v1/users/me/skills/skill/${skillId}`,
      getToken(),
    );
  },
};

export default skillService;