package com.recipes.FlavourFoundry.model;

import jakarta.validation.constraints.NotBlank;

public class Report {
    private Integer recipeId;
    private String id;
    private String category;

    @NotBlank(message = "Subject is mandatory")
    private String subject;

    @NotBlank(message = "Issue is mandatory")
    private String issue;

    private Long date;

    public Report() {}

    public Report(Integer recipeId, String id, String category, String subject, String issue, Long date) {
        this.recipeId = recipeId;
        this.id = id;
        this.category = category;
        this.subject = subject;
        this.issue = issue;
        this.date = date;
    }

    public Integer getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getIssue() {
        return issue;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return recipeId + "|||" + id + "|||" + category + "|||" + subject + "|||" + issue + "|||" + date;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    

}
