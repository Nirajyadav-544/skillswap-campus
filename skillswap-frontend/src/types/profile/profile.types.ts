export interface Profile {
  id: number;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  emailVerified: boolean;

  headline?: string;
  bio?: string;
  college?: string;
  department?: string;
  course?: string;
  yearOfStudy?: number;
  graduationYear?: number;
  location?: string;
  profileImageUrl?: string;

  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  availability?: string;

  profileCompleted: boolean;

  createdAt?: string;
  updatedAt?: string;
}

export interface ProfileSummary {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  headline?: string;
  college?: string;
  department?: string;
  course?: string;
  yearOfStudy?: number;
  graduationYear?: number;
  location?: string;
  profileImageUrl?: string;
  profileCompleted: boolean;
}

export interface UpdateProfileRequest {
  headline?: string;
  bio?: string;
  college?: string;
  department?: string;
  course?: string;
  yearOfStudy?: number;
  graduationYear?: number;
  location?: string;
  profileImageUrl?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  availability?: string;
}

export interface CreateProfileRequest {
  headline?: string;
  bio?: string;
  college?: string;
  department?: string;
  course?: string;
  yearOfStudy?: number;
  graduationYear?: number;
  location?: string;
  profileImageUrl?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  availability?: string;
}

export interface ProfileStats {
  skillsCount: number;
  exchangesCount: number;
  projectsCount: number;
  teamsCount: number;
}

export interface ProfileExistsResponse {
  exists: boolean;
}

export interface ProfileCompletionResponse {
  completed: boolean;
}
