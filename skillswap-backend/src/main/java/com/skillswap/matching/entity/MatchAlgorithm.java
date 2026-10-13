package com.skillswap.matching.entity;

/**
 * Identifies the algorithm that generated a match.
 *
 * Phase 4 starts with a deterministic rule-based engine.
 * Future versions can introduce embedding-based, ML-based,
 * or LLM-assisted matching without changing the core
 * MatchResult structure.
 */
public enum MatchAlgorithm {

    /**
     * Initial Phase 4 deterministic matching algorithm.
     *
     * Uses:
     * - teaching/learning compatibility
     * - skill compatibility
     * - proficiency
     * - experience
     * - profile information
     * - availability/preferences
     */
    RULE_BASED,

    /**
     * Future semantic matching using vector embeddings.
     *
     * This is intentionally defined now so that the data model
     * can distinguish semantic matches from rule-based matches
     * when the AI layer is introduced.
     */
    EMBEDDING_BASED,

    /**
     * Future machine-learning based matching.
     */
    MACHINE_LEARNING,

    /**
     * Future LLM-assisted matching.
     */
    LLM_ASSISTED,

    /**
     * Future hybrid approach combining multiple
     * matching strategies.
     */
    HYBRID
}