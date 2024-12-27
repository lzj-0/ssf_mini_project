package com.recipes.FlavourFoundry.repository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.recipes.FlavourFoundry.model.Recipe;

@Repository
public class RecipeRepo {

    @Autowired
    @Qualifier("recipeTemplate")
    RedisTemplate<String, String> template;

    public String addRecipe(Recipe recipe) {
        // template.opsForHash().put(recipe.getId().toString(), "title", recipe.getTitle());
        // template.opsForHash().put(recipe.getId().toString(), "ingredients", recipe.getIngredients().toString());
        // template.opsForHash().put(recipe.getId().toString(), "readyInMinutes", recipe.getReadyInMinutes().toString());
        // template.opsForHash().put(recipe.getId().toString(), "summary", recipe.getSummary());
        // template.opsForHash().put(recipe.getId().toString(), "instructions", recipe.getInstructions().toString());
        
        // if (recipe.getImageUrl() != null) {
        //     template.opsForHash().put(recipe.getId().toString(), "imageUrl", recipe.getImageUrl());
        // } else if (recipe.getImageData() != null) {
        //     template.opsForHash().put(recipe.getId().toString(), "imageData", Base64.getEncoder().encodeToString(recipe.getImageData()));
        // }

        template.opsForValue().set(recipe.getId().toString(), recipe.toString());

        return recipe.getId().toString();

    }

    public String getRecipeById(String id) {
        //System.out.println(template.opsForValue().get(id));
        return template.opsForValue().get(id);
    }

    public Set<String> getAllRecipeKeys() {
        // System.out.println(template.keys("*").toString());
        return template.keys("*");
    }

    public Integer getCount() {
        return getAllRecipeKeys().size();
    }

    public void deleteRecipebyId(String id) {
        template.opsForValue().getAndDelete(id);
    }

    public String editRecipe(String id, Recipe recipe) {
        template.opsForValue().set(id, recipe.toString());

        return recipe.getId().toString();
    }

}
