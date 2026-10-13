package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeType;
import jakarta.validation.constraints.Size;

public class UpdateExchangeRequest {

    private ExchangeType exchangeType;

    @Size(max = 2000, message = "Request message must not exceed 2000 characters")
    private String requestMessage;

    @Size(max = 200, message = "Requested skill name must not exceed 200 characters")
    private String requestedSkillName;

    @Size(max = 200, message = "Offered skill name must not exceed 200 characters")
    private String offeredSkillName;

    public UpdateExchangeRequest() {
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public void setRequestedSkillName(String requestedSkillName) {
        this.requestedSkillName = requestedSkillName;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
    }

    public void normalize() {
        if (requestMessage != null) {
            requestMessage = requestMessage.trim();
        }

        if (requestedSkillName != null) {
            requestedSkillName = requestedSkillName.trim();
        }

        if (offeredSkillName != null) {
            offeredSkillName = offeredSkillName.trim();
        }
    }

    public boolean isEmpty() {
        return exchangeType == null
                && requestMessage == null
                && requestedSkillName == null
                && offeredSkillName == null;
    }
}