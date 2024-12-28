package com.recipes.FlavourFoundry.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.model.Url;
import com.recipes.FlavourFoundry.repository.RecipeRepo;
import com.recipes.FlavourFoundry.repository.ReportRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    ReportRepo reportRepo;

    @Autowired
    Url url;

    RestTemplate restTemplate = new RestTemplate();

    public List<String> parseList(String listStr, String sep, Boolean labels) throws Exception {
        //System.out.println(listStr);
        String[] input = listStr.split(sep);
        //System.out.println(Arrays.toString(input));
        List<String> list = new ArrayList<>();
        int i = 0;
        for (String line : input) {
            //System.out.println(line + "|||");
            if (line.equals("\n") || line.equals("\r\n") || line.equals("\r")) {
                continue;
            }
            i++;
            String step = "";
            if (labels) {
                if (!line.substring(0, 2).matches(".*\\d.*")) {
                    throw new Exception();
                }
                step = (i > 9) ? line.substring(3).trim() : line.substring(2).trim();
            } else {
                step = line;
            }

            list.add(step);
        }

        return list;
    }

    public String addRecipe(Recipe recipe) {
        Integer count = recipeRepo.getCount();
        count++;
        recipe.setId(count);
        return recipeRepo.addRecipe(recipe);
    }

    public String addRecipe(Recipe recipe, MultipartFile image) throws IOException {
        Integer count = recipeRepo.getCount();
        count++;
        recipe.setId(count);
        if (image.getBytes().length == 0) {
            recipe.setImageUrl("image-placeholder.png");
        } else {
            recipe.setImageData(Base64.getEncoder().encodeToString(image.getBytes()));
            recipe.setImageType(image.getContentType());
        }
        return recipeRepo.addRecipe(recipe);
    }

    public Recipe getRecipeById(String id) throws Exception {
        String recipeStr = recipeRepo.getRecipeById(id);
        //System.out.println(recipeStr); 

        Recipe r = new Recipe();
        String[] input = recipeStr.split("~-~");
        r.setId(Integer.parseInt(input[0]));
        r.setTitle(input[1]);
        r.setIngredients(parseList(input[2], "\\|\\|\\|", false));
        r.setReadyInMinutes(Integer.parseInt(input[3]));
        r.setSummary(input[4]);
        r.setInstructions(parseList(input[5], "\\|\\|\\|", false));
        r.setImageUrl(input[6].equals("null") ? null : input[6]);
        r.setImageData(input[7].equals("null") ? null : input[7]);
        r.setImageType(input[8].equals("null") ? null : input[8]);

        return r;

    }

    public List<Recipe> getAllRecipe() throws Exception {
        Set<String> recipesStr = recipeRepo.getAllRecipeKeys();

        List<Recipe> recipes = new ArrayList<>();
        for (String id : recipesStr) {
            recipes.add(getRecipeById(id));
        }
        recipes.sort((x, y) -> x.getId().compareTo(y.getId()));

        return recipes;

    }

    public List<Recipe> getRandomRecipe(String count, String include, String exclude) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url.getRecipeControllerApi())
                                        .queryParam("number", count);

        if (!include.equals("")) {
            urlBuilder.queryParam("include-tags", include);
        }

        if (!exclude.equals("")) {
            urlBuilder.queryParam("exclude-tags", exclude);
        }

        String url = urlBuilder.toUriString();

        System.out.println(url);


        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);

        JsonReader jReader = Json.createReader(new StringReader(resp.getBody()));
        JsonArray recipesJson = jReader.readObject().getJsonArray("recipes");

        List<Recipe> recipes = new ArrayList<>();
        for (JsonValue recipeVal : recipesJson) {
            JsonObject recipe = recipeVal.asJsonObject();
            Recipe r = new Recipe();

            try {
                r.setTitle(recipe.getString("title"));
            } catch(Exception e) {
                r.setTitle(null);
            }

            try {
                List<String> ingredients = new ArrayList<>();
                for (JsonValue ingredientsVal : recipe.getJsonArray("extendedIngredients")) {
                    ingredients.add(ingredientsVal.asJsonObject().getString("original"));
                }
    
                r.setIngredients(ingredients);
            } catch(Exception e) {
                r.setIngredients(null);
            }


            try {
                r.setReadyInMinutes(recipe.getInt("readyInMinutes"));
            } catch(Exception e) {
                r.setReadyInMinutes(null);
            }

            try {
                r.setSummary(recipe.getString("summary"));
            } catch(Exception e) {
                r.setSummary(null);
            }

            try {
                List<String> instructions = new ArrayList<>();
                for (JsonValue instructionsVal : recipe.getJsonArray("analyzedInstructions").getFirst().asJsonObject().getJsonArray("steps")) {
                    instructions.add(instructionsVal.asJsonObject().getString("step"));
                }
    
                r.setInstructions(instructions);
            } catch(Exception e) {
                r.setInstructions(null);
            }

            try {
                r.setImageUrl(recipe.getString("image"));
            } catch(Exception e) {
                r.setImageUrl("/image-placeholder.png");
            }

            try {
                r.setSource(recipe.getString("sourceName"));
                if (r.getSource().equals("foodista.com")) {
                    r.setSource("Foodista");
                }
                if (r.getSource().equals("pinkwhen.com")) {
                    r.setSource("Pink When");
                }
            } catch(Exception e) {
                r.setSource(null);
            }

            try {
                r.setLink(recipe.getString("sourceUrl"));
            } catch(Exception e) {
                r.setLink(null);
            }

            recipes.add(r);
        }

        return recipes;

    }

    public List<Recipe> searchRecipe(String search) throws Exception {
        String queryString = search.trim().toLowerCase();
        List<Recipe> recipes = getAllRecipe();
        return recipes.stream().filter(x -> x.getTitle().toLowerCase().contains(queryString) || 
                                    x.getIngredients().stream().anyMatch(y -> y.toLowerCase().contains(queryString)) ||
                                    x.getSummary().toLowerCase().contains(queryString) ||
                                    x.getInstructions().stream().anyMatch(y -> y.toLowerCase().contains(queryString)))
                                    .collect(Collectors.toList());
    }




}
