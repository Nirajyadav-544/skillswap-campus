export interface MatchResult {
  id: number;
  matchedUserId: number;
  matchedUserName?: string;
  skillName?: string;
  matchScore?: number;
  matchReason?: string;
}

export interface MatchFilters {
  skill?: string;
  category?: string;
  minScore?: number;
}

export interface MatchSearchRequest {
  skill?: string;
  category?: string;
  page?: number;
  size?: number;
}