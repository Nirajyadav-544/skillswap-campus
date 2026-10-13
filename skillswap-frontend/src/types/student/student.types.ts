export interface StudentSummary {
  id: number;
  userId: number;
  firstName?: string | null;
  lastName?: string | null;
  headline?: string | null;
  college?: string | null;
  department?: string | null;
  course?: string | null;
  yearOfStudy?: number | null;
  graduationYear?: number | null;
  location?: string | null;
  profileImageUrl?: string | null;
  profileCompleted: boolean;
}

export interface StudentProfile {
  id: number;
  userId: number;

  email?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  role?: string | null;
  emailVerified: boolean;

  headline?: string | null;
  bio?: string | null;

  college?: string | null;
  department?: string | null;
  course?: string | null;
  yearOfStudy?: number | null;
  graduationYear?: number | null;

  location?: string | null;
  profileImageUrl?: string | null;

  githubUrl?: string | null;
  linkedinUrl?: string | null;
  portfolioUrl?: string | null;

  availability?: string | null;

  profileCompleted: boolean;

  createdAt?: string | null;
  updatedAt?: string | null;
}

export function getStudentName(student: {
  firstName?: string | null;
  lastName?: string | null;
}): string {
  const firstName = student.firstName?.trim() ?? "";
  const lastName = student.lastName?.trim() ?? "";

  const fullName = `${firstName} ${lastName}`.trim();

  return fullName || "Student";
}