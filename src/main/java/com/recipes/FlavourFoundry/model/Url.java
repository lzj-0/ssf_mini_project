package com.recipes.FlavourFoundry.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Url {

    @Value("${internal.api}")
    private String internalApi;

    public String getRecipeApi() {
        return "https://api.spoonacular.com/recipes/random";
    }
    
    public String getRecipeControllerApi() {
        if (internalApi.contains("localhost")) {
            return internalApi + "/api/getrecipe";
        } else {
            return "https://" + internalApi + "/api/getrecipe";
        }
    }

}

