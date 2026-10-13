import apiClient from "../api/apiClient";

import type {
  MatchResult,
  MatchSearchRequest,
} from "../../types/matching/matching.types";

const matchingService = {
  async getMatches(
    params?: MatchSearchRequest
  ): Promise<MatchResult[]> {
    void params;

    throw new Error(
      "Matching API endpoint is not connected yet."
    );
  },

  async getMatchById(
    matchId: number
  ): Promise<MatchResult> {
    void matchId;

    throw new Error(
      "Match details API endpoint is not connected yet."
    );
  },
};

void apiClient;

export default matchingService;