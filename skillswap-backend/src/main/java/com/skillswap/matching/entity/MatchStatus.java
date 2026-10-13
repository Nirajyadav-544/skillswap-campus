package com.skillswap.matching.entity;

/**
 * Represents the lifecycle/status of a generated match.
 *
 * A match is initially calculated by the matching engine.
 * As the user interacts with the recommendation, its status
 * can move through the lifecycle represented by this enum.
 */
public enum MatchStatus {

    /**
     * Match has been calculated by the matching engine
     * but has not yet been presented/interacted with.
     */
    CALCULATED,

    /**
     * Match recommendation has been shown/opened by the user.
     */
    VIEWED,

    /**
     * User has expressed interest in this match.
     */
    INTERESTED,

    /**
     * User has explicitly dismissed this match.
     */
    DISMISSED,

    /**
     * Match is no longer considered active by the system.
     */
    EXPIRED,

    /**
     * Match has been archived and should not normally
     * appear in active recommendations.
     */
    ARCHIVED
}