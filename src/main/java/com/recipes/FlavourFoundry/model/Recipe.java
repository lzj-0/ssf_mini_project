package com.recipes.FlavourFoundry.model;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Recipe {

    private Integer id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, message = "Title has to have a minimum of 3 characters")
    private String title;

    private List<String> ingredients;

    @Min(value = 5, message = "Preparation time has to be more than 5 minutes")
    @NotNull(message = "Preparation time is mandatory")
    private Integer readyInMinutes;

    @NotBlank(message = "Summary is mandatory")
    private String summary;

    private List<String> instructions;
    private String imageUrl;
    private String imageData;
    private String imageType;
    private String source;
    private String link;

    public Recipe() {}

    public Recipe(String title, List<String> ingredients, Integer readyInMinutes, String summary,
            List<String> instructions) {
        this.title = title;
        this.ingredients = ingredients;
        this.readyInMinutes = readyInMinutes;
        this.summary = summary;
        this.instructions = instructions;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }
    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public List<String> getInstructions() {
        return instructions;
    }
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return id + "~-~" + title + "~-~" + String.join("|||", ingredients) + "~-~"
                + readyInMinutes + "~-~" + summary + "~-~" + String.join("|||", instructions) + "~-~" + imageUrl
                + "~-~" + imageData + "~-~" + imageType;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
