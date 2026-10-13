import apiClient from "../api/apiClient";
import type { StudentProfile, StudentSummary } from "../../types/student/student.types";

const studentService = {
  async getStudents(): Promise<StudentSummary[]> {
    return apiClient.get<StudentSummary[]>(
      "/v1/profile/discover"
    );
  },

  async getStudentProfile(
    userId: number
  ): Promise<StudentProfile> {
    return apiClient.get<StudentProfile>(
      `/v1/profile/user/${userId}`
    );
  },
};

export default studentService;