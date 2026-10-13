import type {
  ProficiencyLevel,
  SkillCategory,
  SkillType,
} from "./skill.enums";

/**
 * Full Skill response.
 *
 * Matches backend:
 * com.skillswap.skill.dto.SkillResponse
 */
export interface Skill {
  id: number;
  name: string;
  description: string | null;
  category: SkillCategory;
  active: boolean;
  systemDefined: boolean;
  popularityScore: number;
  userCount: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * Lightweight Skill response.
 *
 * Matches backend:
 * com.skillswap.skill.dto.SkillSummaryResponse
 *
 * Used by search, popular skills, most-used skills,
 * autocomplete, discovery and skill selection.
 */
export interface SkillSummary {
  id: number;
  name: string;
  category: SkillCategory;
  active: boolean;
  userCount: number;
  popularityScore: number;
}

/**
 * Full UserSkill response.
 *
 * Matches backend:
 * com.skillswap.skill.dto.UserSkillResponse
 */
export interface UserSkill {
  id: number;
  userId: number | null;

  skillId: number | null;
  skillName: string | null;
  skillDescription: string | null;
  skillCategory: SkillCategory | null;

  skillType: SkillType;
  proficiencyLevel: ProficiencyLevel;

  yearsOfExperience: number;
  description: string | null;

  active: boolean;
  verified: boolean;

  exchangeCount: number;
  successfulExchangeCount: number;

  averageRating: number;
  ratingCount: number;

  canTeach: boolean;
  wantsToLearn: boolean;
  experienced: boolean;
  hasRatings: boolean;

  createdAt: string;
  updatedAt: string;
  lastUsedAt: string | null;
}

/**
 * Lightweight UserSkill response.
 *
 * Matches backend:
 * com.skillswap.skill.dto.UserSkillSummaryResponse
 */
export interface UserSkillSummary {
  id: number;
  userId: number | null;

  skillId: number | null;
  skillName: string | null;
  skillCategory: SkillCategory | null;

  skillType: SkillType;
  proficiencyLevel: ProficiencyLevel;

  yearsOfExperience: number;

  active: boolean;
  verified: boolean;

  exchangeCount: number;
  successfulExchangeCount: number;

  averageRating: number;
  ratingCount: number;

  firstName: string | null;
  lastName: string | null;

  canTeach: boolean;
  wantsToLearn: boolean;
  experienced: boolean;
}

/**
 * Generic Spring Data page response.
 *
 * This matches the standard JSON structure returned by
 * Spring Data Page<T> used by the backend controllers.
 */
export interface ApiPage<T> {
  content: T[];

  pageable?: {
    pageNumber?: number;
    pageSize?: number;
    sort?: {
      empty?: boolean;
      sorted?: boolean;
      unsorted?: boolean;
    };
    offset?: number;
    paged?: boolean;
    unpaged?: boolean;
  };

  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort?: {
    empty?: boolean;
    sorted?: boolean;
    unsorted?: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

export interface CreateSkillRequest {
  name: string;
  description?: string | null;
  category: SkillCategory;
}

export interface UpdateSkillRequest {
  name: string;
  description?: string | null;
  category: SkillCategory;
  active: boolean;
}

export interface AddUserSkillRequest {
  skillId: number;
  skillType: SkillType;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience: number;
  description?: string | null;
}

export interface UpdateUserSkillRequest {
  skillType: SkillType;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience: number;
  description?: string | null;
  active: boolean;
}