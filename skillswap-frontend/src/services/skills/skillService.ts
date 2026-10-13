import apiClient from "../api/apiClient";

import type {
  AddUserSkillRequest,
  Skill,
  SkillPageResponse,
  SkillSearchParams,
  SkillSummary,
  UpdateSkillRequest,
  UpdateUserSkillRequest,
  UserSkill,
  UserSkillSummary,
  CreateSkillRequest,
} from "../../types/skills/skill.types";

const skillService = {
  // =========================================================
  // SKILL CATALOG
  // =========================================================

  async getAllSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>("/api/v1/skills");
  },

  async getActiveSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>("/api/v1/skills/active");
  },

  async getPopularSkills(): Promise<SkillSummary[]> {
    return apiClient.get<SkillSummary[]>("/api/v1/skills/popular");
  },

  async getMostUsedSkills(): Promise<SkillSummary[]> {
    return apiClient.get<SkillSummary[]>("/api/v1/skills/most-used");
  },

  async getSystemDefinedSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>("/api/v1/skills/system");
  },

  async getCustomSkills(): Promise<Skill[]> {
    return apiClient.get<Skill[]>("/api/v1/skills/custom");
  },

  async searchSkills(query?: string): Promise<SkillSummary[]> {
    const params = new URLSearchParams();

    if (query?.trim()) {
      params.set("q", query.trim());
    }

    const queryString = params.toString();

    return apiClient.get<SkillSummary[]>(
      queryString
        ? `/api/v1/skills/search?${queryString}`
        : "/api/v1/skills/search"
    );
  },

  async searchSkillsPaginated(
    params: SkillSearchParams = {}
  ): Promise<SkillPageResponse<SkillSummary>> {
    const query = new URLSearchParams();

    if (params.search?.trim()) {
      query.set("q", params.search.trim());
    }

    query.set("page", String(params.page ?? 0));
    query.set("size", String(params.size ?? 20));

    return apiClient.get<SkillPageResponse<SkillSummary>>(
      `/api/v1/skills/search/page?${query.toString()}`
    );
  },

  async getSkillsByCategory(
    category: string
  ): Promise<Skill[]> {
    return apiClient.get<Skill[]>(
      `/api/v1/skills/category/${encodeURIComponent(category)}`
    );
  },

  async getSkillById(skillId: number): Promise<Skill> {
    return apiClient.get<Skill>(
      `/api/v1/skills/${skillId}`
    );
  },

  async getSkillByName(name: string): Promise<Skill> {
    return apiClient.get<Skill>(
      `/api/v1/skills/name/${encodeURIComponent(name)}`
    );
  },

  async createSkill(
    data: CreateSkillRequest
  ): Promise<Skill> {
    return apiClient.post<Skill>(
      "/api/v1/skills",
      data
    );
  },

  async updateSkill(
    skillId: number,
    data: UpdateSkillRequest
  ): Promise<Skill> {
    return apiClient.put<Skill>(
      `/api/v1/skills/${skillId}`,
      data
    );
  },

  async activateSkill(skillId: number): Promise<Skill> {
    return apiClient.patch<Skill>(
      `/api/v1/skills/${skillId}/activate`
    );
  },

  async deactivateSkill(skillId: number): Promise<Skill> {
    return apiClient.patch<Skill>(
      `/api/v1/skills/${skillId}/deactivate`
    );
  },

  async deleteSkill(skillId: number): Promise<void> {
    await apiClient.delete(
      `/api/v1/skills/${skillId}`
    );
  },

  // =========================================================
  // MY SKILLS
  // =========================================================

  async getMySkills(): Promise<UserSkill[]> {
    return apiClient.get<UserSkill[]>(
      "/api/v1/users/me/skills"
    );
  },

  async getMySkillsPaginated(
    page = 0,
    size = 20
  ): Promise<SkillPageResponse<UserSkillSummary>> {
    return apiClient.get<SkillPageResponse<UserSkillSummary>>(
      `/api/v1/users/me/skills/page?page=${page}&size=${size}`
    );
  },

  async getMyActiveSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/api/v1/users/me/skills/active"
    );
  },

  async getMyTeachingSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/api/v1/users/me/skills/teaching"
    );
  },

  async getMyLearningSkills(): Promise<UserSkillSummary[]> {
    return apiClient.get<UserSkillSummary[]>(
      "/api/v1/users/me/skills/learning"
    );
  },

  async getMySkill(
    userSkillId: number
  ): Promise<UserSkill> {
    return apiClient.get<UserSkill>(
      `/api/v1/users/me/skills/${userSkillId}`
    );
  },

  async getMySkillBySkillId(
    skillId: number
  ): Promise<UserSkill> {
    return apiClient.get<UserSkill>(
      `/api/v1/users/me/skills/skill/${skillId}`
    );
  },

  async addMySkill(
    data: AddUserSkillRequest
  ): Promise<UserSkill> {
    return apiClient.post<UserSkill>(
      "/api/v1/users/me/skills",
      data
    );
  },

  async updateMySkill(
    userSkillId: number,
    data: UpdateUserSkillRequest
  ): Promise<UserSkill> {
    return apiClient.put<UserSkill>(
      `/api/v1/users/me/skills/${userSkillId}`,
      data
    );
  },

  async activateMySkill(
    userSkillId: number
  ): Promise<UserSkill> {
    return apiClient.patch<UserSkill>(
      `/api/v1/users/me/skills/${userSkillId}/activate`
    );
  },

  async deactivateMySkill(
    userSkillId: number
  ): Promise<UserSkill> {
    return apiClient.patch<UserSkill>(
      `/api/v1/users/me/skills/${userSkillId}/deactivate`
    );
  },

  async deleteMySkill(
    userSkillId: number
  ): Promise<void> {
    await apiClient.delete(
      `/api/v1/users/me/skills/${userSkillId}`
    );
  },

  async deleteMySkillBySkillId(
    skillId: number
  ): Promise<void> {
    await apiClient.delete(
      `/api/v1/users/me/skills/skill/${skillId}`
    );
  },
};

export default skillService;
