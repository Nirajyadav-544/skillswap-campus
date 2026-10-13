/**
 * Skill categories supported by the SkillSwap Campus backend.
 *
 * These values match:
 * com.skillswap.skill.entity.SkillCategory
 */

export const SkillCategory = {
  PROGRAMMING: "PROGRAMMING",
  WEB_DEVELOPMENT: "WEB_DEVELOPMENT",
  MOBILE_DEVELOPMENT: "MOBILE_DEVELOPMENT",
  SOFTWARE_ENGINEERING: "SOFTWARE_ENGINEERING",
  DATA_SCIENCE: "DATA_SCIENCE",
  ARTIFICIAL_INTELLIGENCE: "ARTIFICIAL_INTELLIGENCE",
  MACHINE_LEARNING: "MACHINE_LEARNING",
  CYBER_SECURITY: "CYBER_SECURITY",
  CLOUD_DEVOPS: "CLOUD_DEVOPS",
  DATABASE: "DATABASE",
  DESIGN: "DESIGN",
  UI_UX: "UI_UX",
  BUSINESS: "BUSINESS",
  ENTREPRENEURSHIP: "ENTREPRENEURSHIP",
  MARKETING: "MARKETING",
  FINANCE: "FINANCE",
  COMMUNICATION: "COMMUNICATION",
  LEADERSHIP: "LEADERSHIP",
  PUBLIC_SPEAKING: "PUBLIC_SPEAKING",
  LANGUAGES: "LANGUAGES",
  WRITING: "WRITING",
  RESEARCH: "RESEARCH",
  ACADEMIC: "ACADEMIC",
  CREATIVE: "CREATIVE",
  PHOTOGRAPHY: "PHOTOGRAPHY",
  VIDEO_EDITING: "VIDEO_EDITING",
  MUSIC: "MUSIC",
  SPORTS: "SPORTS",
  PERSONAL_DEVELOPMENT: "PERSONAL_DEVELOPMENT",
  CAREER: "CAREER",
  OTHER: "OTHER",
} as const;

export type SkillCategory =
  (typeof SkillCategory)[keyof typeof SkillCategory];


/**
 * Defines how a user wants to use a skill.
 *
 * These values match:
 * com.skillswap.skill.entity.SkillType
 */
export const SkillType = {
  TEACH: "TEACH",
  LEARN: "LEARN",
  BOTH: "BOTH",
} as const;

export type SkillType =
  (typeof SkillType)[keyof typeof SkillType];


/**
 * User proficiency levels.
 *
 * These values match:
 * com.skillswap.skill.entity.ProficiencyLevel
 */
export const ProficiencyLevel = {
  BEGINNER: "BEGINNER",
  INTERMEDIATE: "INTERMEDIATE",
  ADVANCED: "ADVANCED",
  EXPERT: "EXPERT",
} as const;

export type ProficiencyLevel =
  (typeof ProficiencyLevel)[keyof typeof ProficiencyLevel];