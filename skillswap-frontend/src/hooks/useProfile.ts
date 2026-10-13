
import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type {
  CreateProfileRequest,
  Profile,
  UpdateProfileRequest,
} from "../types/profile/profile.types";

import profileService from "../services/profile/profileService";

export function useProfile() {
  const [profile, setProfile] =
    useState<Profile | null>(null);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  /*
   * Create profile
   *
   * Kept for compatibility with the existing
   * profile service and other components.
   */
  const createProfile = useCallback(
    async (data: CreateProfileRequest) => {
      setLoading(true);
      setError(null);

      try {
        const createdProfile =
          await profileService.createProfile(data);

        setProfile(createdProfile);

        return createdProfile;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Unable to create profile.";

        setError(message);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /*
   * Load the currently logged-in user's profile.
   */
  const loadProfile = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data =
        await profileService.getMyProfile();

      setProfile(data);

      return data;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Unable to load profile.";

      setError(message);

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  /*
   * Automatically load the existing profile
   * when the profile page/component is mounted.
   *
   * This was missing previously and was the reason
   * the page stayed on "Preparing your profile...".
   */
  useEffect(() => {
    let mounted = true;

    const initializeProfile = async () => {
      try {
        await loadProfile();
      } catch {
        /*
         * loadProfile already stores the error in state.
         * Nothing else is required here.
         */
      }

      if (!mounted) {
        return;
      }
    };

    void initializeProfile();

    return () => {
      mounted = false;
    };
  }, [loadProfile]);

  /*
   * Update existing profile.
   *
   * The returned profile is stored immediately in state,
   * so the Profile page can display the saved data.
   */
  const updateProfile = useCallback(
    async (data: UpdateProfileRequest) => {
      setLoading(true);
      setError(null);

      try {
        const updatedProfile =
          await profileService.updateProfile(data);

        setProfile(updatedProfile);

        return updatedProfile;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Unable to update profile.";

        setError(message);

        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /*
   * Delete profile
   *
   * Kept for compatibility with the existing service.
   */
  const deleteProfile = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      await profileService.deleteMyProfile();

      setProfile(null);

      return true;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Unable to delete profile.";

      setError(message);

      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  /*
   * Check whether a profile exists.
   */
  const checkProfileExists = useCallback(async () => {
    try {
      const response =
        await profileService.profileExists();

      return response.exists;
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Unable to check profile.";

      setError(message);

      return false;
    }
  }, []);

  /*
   * Check profile completion.
   */
  const checkProfileCompletion =
    useCallback(async () => {
      try {
        const response =
          await profileService.profileCompletion();

        return response.completed;
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : "Unable to check profile completion.";

        setError(message);

        return false;
      }
    }, []);

  /*
   * Clear current profile error.
   */
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    profile,
    loading,
    error,

    createProfile,
    loadProfile,
    updateProfile,
    deleteProfile,

    checkProfileExists,
    checkProfileCompletion,

    clearError,
  };
}

export default useProfile;
