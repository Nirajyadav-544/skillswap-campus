export const APP_NAME = "SkillSwap Campus";

export const STORAGE_KEYS = {
  ACCESS_TOKEN: "skillswap_access_token",
  USER: "skillswap_user",
} as const;

export const API = {
  AUTH: {
    LOGIN: "/api/v1/auth/login",
    REGISTER: "/api/v1/auth/register",
  },

  TEAMS: "/api/v1/teams",

  EXCHANGE: {
    REQUESTS: "/api/v1/exchange/requests",
    OFFERS: "/api/v1/exchange/offers",
    DISCOVERY: "/api/v1/exchange/discovery",
  },

  INTERVIEW: "/api/v1/interview",
} as const;

export const ROUTES = {
  HOME: "/",
  LOGIN: "/login",
  REGISTER: "/register",
  VERIFY_OTP: "/verify-otp",
  DASHBOARD: "/dashboard",
  PROFILE: "/profile",
  SKILLS: "/skills",
  EXCHANGE: "/exchange",
  TEAMS: "/teams",
  INTERVIEW: "/interview",
  CAREER: "/career",
  PROJECTS: "/projects",
  AI: "/ai",
  SETTINGS: "/settings",
} as const;