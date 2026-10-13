package com.skillswap.search.dto;

public class SearchResultResponse {

    private String resultType;

    private Long resultId;

    private String title;

    private String description;

    private String url;

    private Double score;

    private String matchedText;

    public SearchResultResponse() {
    }

    public SearchResultResponse(
            String resultType,
            Long resultId,
            String title,
            String description,
            String url,
            Double score,
            String matchedText
    ) {
        this.resultType = resultType;
        this.resultId = resultId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.score = score;
        this.matchedText = matchedText;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }
}