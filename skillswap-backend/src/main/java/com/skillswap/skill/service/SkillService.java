package com.skillswap.skill.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.skill.dto.CreateSkillRequest;
import com.skillswap.skill.dto.SkillResponse;
import com.skillswap.skill.dto.SkillSummaryResponse;
import com.skillswap.skill.dto.UpdateSkillRequest;
import com.skillswap.skill.entity.Skill;
import com.skillswap.skill.entity.SkillCategory;
import com.skillswap.skill.repository.SkillRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional
public class SkillService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // =========================================================
    // CREATE
    // =========================================================

    public SkillResponse createSkill(CreateSkillRequest request) {

        if (request == null) {
            throw new BadRequestException("Skill request cannot be null");
        }

        String name = normalizeDisplayName(request.name());
        String normalizedName = normalizeSkillName(name);

        validateSkillName(name);

        if (skillRepository.existsByNormalizedName(normalizedName)) {
            throw new BadRequestException(
                    "A skill with the name '" + name + "' already exists"
            );
        }

        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException(
                    "A skill with the name '" + name + "' already exists"
            );
        }

        Skill skill = new Skill();

        skill.setName(name);
        skill.setNormalizedName(normalizedName);
        skill.setDescription(normalizeDescription(request.description()));
        skill.setCategory(
                request.category() != null
                        ? request.category()
                        : SkillCategory.OTHER
        );
        skill.setActive(true);
        skill.setSystemDefined(false);
        skill.setPopularityScore(0);
        skill.setUserCount(0);

        Skill savedSkill = skillRepository.save(skill);

        return toResponse(savedSkill);
    }

    // =========================================================
    // CREATE SYSTEM SKILL
    // =========================================================

    public SkillResponse createSystemSkill(CreateSkillRequest request) {

        if (request == null) {
            throw new BadRequestException("Skill request cannot be null");
        }

        String name = normalizeDisplayName(request.name());
        String normalizedName = normalizeSkillName(name);

        validateSkillName(name);

        if (skillRepository.existsByNormalizedName(normalizedName)) {
            throw new BadRequestException(
                    "A skill with the name '" + name + "' already exists"
            );
        }

        Skill skill = new Skill();

        skill.setName(name);
        skill.setNormalizedName(normalizedName);
        skill.setDescription(normalizeDescription(request.description()));
        skill.setCategory(
                request.category() != null
                        ? request.category()
                        : SkillCategory.OTHER
        );
        skill.setActive(true);
        skill.setSystemDefined(true);
        skill.setPopularityScore(0);
        skill.setUserCount(0);

        return toResponse(skillRepository.save(skill));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional
    public SkillResponse getSkillById(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        return toResponse(skill);
    }

    // =========================================================
    // GET ENTITY
    // =========================================================

    @Transactional
    public Skill getSkillEntity(Long skillId) {

        if (skillId == null || skillId <= 0) {
            throw new BadRequestException("Valid skill ID is required");
        }

        return skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found with ID: " + skillId
                        )
                );
    }

    // =========================================================
    // GET ACTIVE SKILL
    // =========================================================

    @Transactional
    public Skill getActiveSkillEntity(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        if (!skill.isActive()) {
            throw new BadRequestException(
                    "Skill is currently inactive"
            );
        }

        return skill;
    }

    // =========================================================
    // GET BY NAME
    // =========================================================

    @Transactional
    public SkillResponse getSkillByName(String name) {

        String normalizedName = normalizeSkillName(name);

        if (normalizedName.isBlank()) {
            throw new BadRequestException("Skill name is required");
        }

        Skill skill = skillRepository
                .findByNormalizedName(normalizedName)
                .orElseGet(() ->
                        skillRepository.findByNameIgnoreCase(
                                normalizeDisplayName(name)
                        ).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Skill not found: " + name
                                )
                        )
                );

        return toResponse(skill);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public SkillResponse updateSkill(
            Long skillId,
            UpdateSkillRequest request
    ) {

        if (request == null) {
            throw new BadRequestException("Update request cannot be null");
        }

        Skill skill = getSkillEntity(skillId);

        String newName = normalizeDisplayName(request.name());
        String newNormalizedName = normalizeSkillName(newName);

        validateSkillName(newName);

        boolean nameChanged =
                !Objects.equals(
                        skill.getNormalizedName(),
                        newNormalizedName
                );

        if (nameChanged) {

            if (skillRepository.existsByNormalizedName(newNormalizedName)) {
                throw new BadRequestException(
                        "A skill with the name '" +
                                newName +
                                "' already exists"
                );
            }

            skill.setName(newName);
            skill.setNormalizedName(newNormalizedName);
        }

        if (request.description() != null) {
            skill.setDescription(
                    normalizeDescription(request.description())
            );
        }

        if (request.category() != null) {
            skill.setCategory(request.category());
        }

        if (request.active() != null) {
            skill.setActive(request.active());
        }

        return toResponse(skillRepository.save(skill));
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    public SkillResponse activateSkill(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        if (skill.isActive()) {
            return toResponse(skill);
        }

        skill.setActive(true);

        return toResponse(skillRepository.save(skill));
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    public SkillResponse deactivateSkill(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        if (skill.isSystemDefined()) {
            throw new BadRequestException(
                    "System-defined skills cannot be deactivated"
            );
        }

        skill.setActive(false);

        return toResponse(skillRepository.save(skill));
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteSkill(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        if (skill.isSystemDefined()) {
            throw new BadRequestException(
                    "System-defined skills cannot be deleted"
            );
        }

        if (skill.getUserCount() > 0) {
            throw new BadRequestException(
                    "Skill cannot be deleted because users are associated with it"
            );
        }

        skillRepository.delete(skill);
    }

    // =========================================================
    // SEARCH
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse> searchSkills(String query) {

        String normalizedQuery =
                query == null
                        ? ""
                        : query.trim();

        if (normalizedQuery.isBlank()) {
            return getPopularSkills();
        }

        return skillRepository
                .searchActiveSkills(normalizedQuery)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    // =========================================================
    // PAGINATED SEARCH
    // =========================================================

    @Transactional
    public Page<SkillSummaryResponse> searchSkills(
            String query,
            int page,
            int size
    ) {

        page = normalizePage(page);
        size = normalizePageSize(size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "name"
                        )
                );

        String normalizedQuery =
                query == null
                        ? ""
                        : query.trim();

        if (normalizedQuery.isBlank()) {

            return skillRepository
                    .findByActiveTrue(pageable)
                    .map(this::toSummaryResponse);
        }

        return skillRepository
                .searchActiveSkills(
                        normalizedQuery,
                        pageable
                )
                .map(this::toSummaryResponse);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional
    public List<SkillResponse> getAllSkills() {

        return skillRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // GET ACTIVE
    // =========================================================

    @Transactional
    public List<SkillResponse> getActiveSkills() {

        return skillRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // GET BY CATEGORY
    // =========================================================

    @Transactional
    public List<SkillResponse> getSkillsByCategory(
            SkillCategory category
    ) {

        if (category == null) {
            throw new BadRequestException(
                    "Skill category is required"
            );
        }

        return skillRepository
                .findByCategoryAndActiveTrueOrderByNameAsc(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // POPULAR SKILLS
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse> getPopularSkills() {

        return skillRepository
                .findTop20ByActiveTrueOrderByPopularityScoreDesc()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    // =========================================================
    // MOST USED SKILLS
    // =========================================================

    @Transactional
    public List<SkillSummaryResponse> getMostUsedSkills() {

        return skillRepository
                .findTop20ByActiveTrueOrderByUserCountDesc()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    // =========================================================
    // SYSTEM SKILLS
    // =========================================================

    @Transactional
    public List<SkillResponse> getSystemDefinedSkills() {

        return skillRepository
                .findBySystemDefinedTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // CUSTOM SKILLS
    // =========================================================

    @Transactional
    public List<SkillResponse> getCustomSkills() {

        return skillRepository
                .findBySystemDefinedFalseOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // COUNTERS
    // =========================================================

    public void incrementUserCount(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        skill.incrementUserCount();

        skillRepository.save(skill);
    }

    public void decrementUserCount(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        skill.decrementUserCount();

        skillRepository.save(skill);
    }

    public void incrementPopularity(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        skill.incrementPopularityScore();

        skillRepository.save(skill);
    }

    public void decrementPopularity(Long skillId) {

        Skill skill = getSkillEntity(skillId);

        skill.decrementPopularityScore();

        skillRepository.save(skill);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateSkillName(String name) {

        if (name == null || name.isBlank()) {
            throw new BadRequestException(
                    "Skill name is required"
            );
        }

        if (name.length() < 2) {
            throw new BadRequestException(
                    "Skill name must contain at least 2 characters"
            );
        }

        if (name.length() > 150) {
            throw new BadRequestException(
                    "Skill name must not exceed 150 characters"
            );
        }
    }

    // =========================================================
    // NORMALIZATION
    // =========================================================

    private String normalizeDisplayName(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeSkillName(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeDescription(String value) {

        if (value == null) {
            return null;
        }

        String normalized =
                value
                        .trim()
                        .replaceAll("\\s+", " ");

        return normalized.isBlank()
                ? null
                : normalized;
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    private int normalizePage(int page) {

        return Math.max(page, 0);
    }

    private int normalizePageSize(int size) {

        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(size, MAX_PAGE_SIZE);
    }

    // =========================================================
    // DTO MAPPING
    // =========================================================

    private SkillResponse toResponse(Skill skill) {

        if (skill == null) {
            throw new IllegalArgumentException(
                    "Skill cannot be null"
            );
        }

        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getDescription(),
                skill.getCategory(),
                skill.isActive(),
                skill.isSystemDefined(),
                skill.getPopularityScore(),
                skill.getUserCount(),
                skill.getCreatedAt(),
                skill.getUpdatedAt()
        );
    }

    private SkillSummaryResponse toSummaryResponse(
            Skill skill
    ) {

        if (skill == null) {
            throw new IllegalArgumentException(
                    "Skill cannot be null"
            );
        }

        return new SkillSummaryResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                skill.isActive(),
                skill.getUserCount(),
                skill.getPopularityScore()
        );
    }
}