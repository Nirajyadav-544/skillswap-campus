package com.skillswap.exchange.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ExchangeOfferRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 200, message = "Skill name must not exceed 200 characters")
    private String skillName;

    private Long skillId;

    @Size(
            max = 2000,
            message = "Offer description must not exceed 2000 characters"
    )
    private String offerDescription;

    public ExchangeOfferRequest() {
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public void normalize() {
        if (skillName != null) {
            skillName = skillName.trim();
        }

        if (offerDescription != null) {
            offerDescription = offerDescription.trim();
        }
    }
}