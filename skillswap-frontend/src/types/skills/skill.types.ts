
import type {
  SkillCategory,
  SkillType,
  ProficiencyLevel,
} from "../skill/skill.enums";

import type {
  Skill,
  SkillSummary,
  UserSkill,
  UserSkillSummary,
  ApiPage,
  CreateSkillRequest,
  UpdateSkillRequest,
  AddUserSkillRequest,
  UpdateUserSkillRequest,
} from "../skill/skill.types";

/**
 * Shared Skill types
 *
 * This file acts as the public types entry point for the
 * `types/skills` structure while the canonical definitions
 * remain inside `types/skill`.
 */
export type {
  Skill,
  SkillSummary,
  UserSkill,
  UserSkillSummary,
  ApiPage,
  CreateSkillRequest,
  UpdateSkillRequest,
  AddUserSkillRequest,
  UpdateUserSkillRequest,
};

/**
 * Skill enums
 */
export type {
  SkillCategory,
  SkillType,
  ProficiencyLevel,
};

/**
 * Skill catalog search parameters.
 */
export type SkillSearchParams = {
  search?: string;
  page?: number;
  size?: number;
};

/**
 * Generic paginated Skill API response.
 *
 * Kept here for compatibility with existing Skill pages/services.
 */
export type SkillPageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

/**
 * Alias used by My Skills components.
 */
export type MySkill = UserSkill;

/**
 * Alias used by Add Skill forms/pages.
 */
export type AddSkillRequest = AddUserSkillRequest;

