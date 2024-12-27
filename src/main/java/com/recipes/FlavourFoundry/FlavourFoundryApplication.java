package com.recipes.FlavourFoundry;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.service.RecipeService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@SpringBootApplication
public class FlavourFoundryApplication implements CommandLineRunner {

	@Autowired
	RecipeService recipeService;

	public static void main(String[] args) {
		SpringApplication.run(FlavourFoundryApplication.class, args);
	}
	
	public void run(String... args) throws IOException {
		FileReader fr = new FileReader("recipes.json");
		JsonReader jReader = Json.createReader(fr);
		JsonArray jArray = jReader.readArray();

		for (JsonValue recipeVal : jArray) {
			JsonObject recipeJson = recipeVal.asJsonObject();
			Recipe r = new Recipe();
			r.setTitle(recipeJson.getString("title"));

			List<String> ingredients = new ArrayList<>();
			JsonArray ingredientsArr = recipeJson.getJsonArray("ingredients");
			for (int i = 0; i < ingredientsArr.size(); i++) {
				ingredients.add(ingredientsArr.getString(i));
			}
			r.setIngredients(ingredients);

			r.setReadyInMinutes(recipeJson.getInt("readyInMinutes"));
			r.setSummary(recipeJson.getString("summary"));
			
			List<String> instructions = new ArrayList<>();
			JsonArray instructionsArr = recipeJson.getJsonArray("instructions");
			for (int i = 0; i < instructionsArr.size(); i++) {
				instructions.add(instructionsArr.getString(i));
			}
			r.setInstructions(instructions);

			r.setImageUrl(recipeJson.getString("imageUrl"));

			recipeService.addRecipe(r);
			
		}
	}

}
