import apiClient from "../api/apiClient";
import storageService from "../../utils/storage";

import type {
  CreateProfileRequest,
  Profile,
  ProfileCompletionResponse,
  ProfileExistsResponse,
  ProfileSummary,
  UpdateProfileRequest,
} from "../../types/profile/profile.types";

const getToken = (): string => {
  const token = storageService.getAccessToken();

  if (!token) {
    throw new Error(
      "Authentication token is missing. Please login again.",
    );
  }

  return token;
};

const profileService = {
  async createProfile(
    data: CreateProfileRequest,
  ): Promise<Profile> {
    return apiClient.post<Profile>(
      "/v1/profile",
      data,
      getToken(),
    );
  },

  async getMyProfile(): Promise<Profile> {
    return apiClient.get<Profile>(
      "/v1/profile/me",
      getToken(),
    );
  },

  async updateProfile(
    data: UpdateProfileRequest,
  ): Promise<Profile> {
    return apiClient.put<Profile>(
      "/v1/profile/me",
      data,
      getToken(),
    );
  },

  async deleteMyProfile(): Promise<void> {
    await apiClient.delete<void>(
      "/v1/profile/me",
      getToken(),
    );
  },

  async profileExists(): Promise<ProfileExistsResponse> {
    return apiClient.get<ProfileExistsResponse>(
      "/v1/profile/me/exists",
      getToken(),
    );
  },

  async profileCompletion(): Promise<ProfileCompletionResponse> {
    return apiClient.get<ProfileCompletionResponse>(
      "/v1/profile/me/completion",
      getToken(),
    );
  },

  async getProfileByUserId(
    userId: number,
  ): Promise<Profile> {
    return apiClient.get<Profile>(
      `/v1/profile/user/${userId}`,
      getToken(),
    );
  },

  async getProfileById(
    profileId: number,
  ): Promise<Profile> {
    return apiClient.get<Profile>(
      `/v1/profile/${profileId}`,
      getToken(),
    );
  },

  async discoverProfiles(): Promise<ProfileSummary[]> {
    return apiClient.get<ProfileSummary[]>(
      "/v1/profile/discover",
      getToken(),
    );
  },

  async discoverByCollege(
    college: string,
  ): Promise<ProfileSummary[]> {
    const query = new URLSearchParams({ college });

    return apiClient.get<ProfileSummary[]>(
      `/v1/profile/discover/college?${query.toString()}`,
      getToken(),
    );
  },

  async discoverByDepartment(
    department: string,
  ): Promise<ProfileSummary[]> {
    const query = new URLSearchParams({ department });

    return apiClient.get<ProfileSummary[]>(
      `/v1/profile/discover/department?${query.toString()}`,
      getToken(),
    );
  },

  async discoverByCollegeAndDepartment(
    college: string,
    department: string,
  ): Promise<ProfileSummary[]> {
    const query = new URLSearchParams({
      college,
      department,
    });

    return apiClient.get<ProfileSummary[]>(
      `/v1/profile/discover/filter?${query.toString()}`,
      getToken(),
    );
  },

  async discoverByCourse(
    course: string,
  ): Promise<ProfileSummary[]> {
    const query = new URLSearchParams({ course });

    return apiClient.get<ProfileSummary[]>(
      `/v1/profile/discover/course?${query.toString()}`,
      getToken(),
    );
  },

  async discoverByGraduationYear(
    year: number,
  ): Promise<ProfileSummary[]> {
    const query = new URLSearchParams({
      year: String(year),
    });

    return apiClient.get<ProfileSummary[]>(
      `/v1/profile/discover/graduation-year?${query.toString()}`,
      getToken(),
    );
  },
};

export default profileService;