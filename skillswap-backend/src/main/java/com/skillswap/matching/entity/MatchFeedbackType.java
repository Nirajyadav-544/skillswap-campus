package com.skillswap.matching.entity;

/**
 * Defines the different types of feedback a user can provide
 * about a generated match.
 */
public enum MatchFeedbackType {

    /**
     * User is interested in the matched person.
     */
    INTERESTED,

    /**
     * User found the recommendation helpful.
     */
    HELPFUL,

    /**
     * User considers the recommendation relevant.
     */
    RELEVANT,

    /**
     * User is not interested in the matched person.
     */
    NOT_INTERESTED,

    /**
     * User did not find the recommendation helpful.
     */
    NOT_HELPFUL,

    /**
     * User considers the recommendation not relevant.
     */
    NOT_RELEVANT,

    /**
     * User explicitly dismissed the recommendation.
     */
    DISMISSED
}