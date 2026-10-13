package com.skillswap.skill.entity;

/**
 * Defines how a user wants to use a particular skill
 * within the SkillSwap ecosystem.
 *
 * TEACH  - User can teach this skill.
 * LEARN  - User wants to learn this skill.
 * BOTH   - User can teach and wants to learn this skill.
 */
public enum SkillType {

    TEACH,

    LEARN,

    BOTH
}