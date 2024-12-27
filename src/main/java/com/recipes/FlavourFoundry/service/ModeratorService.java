package com.recipes.FlavourFoundry.service;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.repository.RecipeRepo;


@Service
public class ModeratorService {

    @Value("${moderator.username}")
    private String modUsername;

    @Value("${moderator.password}")
    private String modPassword;

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    RecipeService recipeService;

    public void validate(String username, String password) throws Exception {
        if (!username.equals(modUsername)) {
            throw new Exception("Incorrect username");
        }
        if (!password.equals(modPassword)) {
            throw new Exception("Incorrect password");
        }
    }

    public void deleteRecipebyId(String id) {
        recipeRepo.deleteRecipebyId(id);
    }

    public String editRecipe(String id, Recipe recipe, MultipartFile image) throws Exception {
        recipe.setId(Integer.parseInt(id));
        String prevImage = recipeService.getRecipeById(id).getImageData();
        String prevImageUrl = recipeService.getRecipeById(id).getImageUrl();
        // System.out.println(prevImage);
        // System.out.println(prevImageUrl);
        if (image.getBytes().length == 0 && prevImage == null && prevImageUrl == null) {
            recipe.setImageUrl("image-placeholder.png");
        } else if (image.getBytes().length == 0 && prevImage != null) {
            recipe.setImageData(prevImage);
            recipe.setImageType(recipeService.getRecipeById(id).getImageType());
        } else if (image.getBytes().length == 0 && prevImageUrl != null) {
            recipe.setImageUrl(prevImageUrl);
        } else {
            recipe.setImageData(Base64.getEncoder().encodeToString(image.getBytes()));
            recipe.setImageType(image.getContentType());
        }

        return recipeRepo.editRecipe(id, recipe);
    }

    public String unparseList(List<String> list, String sep, Boolean labels) {
        if (!labels) {
            return String.join("\n", list);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(i+1 + ". " + list.get(i) + sep);
            }
            return sb.toString();
        }
        
    }


}
