package com.recipes.FlavourFoundry.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.model.Report;
import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.ReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/community")
public class CommunityRecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    ReportService reportService;

    @GetMapping("/addrecipe")
    public String addRecipe(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("reportCount", reportService.getReportCount());
        return "addrecipe";
    }

    @PostMapping("/addrecipe")
    public String addRecipePost(@Valid Recipe recipe,
                                BindingResult binding,
                                @RequestParam("ingredients") String ingredientsStr,
                                @RequestParam("instructions") String instructionsStr, 
                                @RequestParam MultipartFile image, Model model, HttpSession session) throws IOException {
        
        if (binding.hasErrors()) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "addrecipe";
        }


        if (ingredientsStr.equals("")) {
            FieldError err = new FieldError("recipe", "ingredients", "Ingredients is mandatory");
            binding.addError(err);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "addrecipe";
        }

        if (instructionsStr.equals("")) {
            FieldError err = new FieldError("recipe", "instructions", "Instructions is mandatory");
            binding.addError(err);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "addrecipe";
        }

        try {
            List<String> ingredients = recipeService.parseList(ingredientsStr, "\n", false);
            recipe.setIngredients(ingredients);
        } catch (Exception e) {
            FieldError err = new FieldError("recipe", "ingredients", "Formatting error. Ensure that you type in only one ingredient per line");
            binding.addError(err);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "addrecipe";
        }

        try {
            List<String> instructions = recipeService.parseList(instructionsStr, "\n", true);
            recipe.setInstructions(instructions);
        } catch (Exception e) {
            FieldError err = new FieldError("recipe", "instructions", "Formatting error. Ensure that you type in only one instruction per line and follow exactly to the numbering format");
            binding.addError(err);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "addrecipe";
        }

        String recipeId = recipeService.addRecipe(recipe, image);

        model.addAttribute("id", recipeId);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());

        return "recipeadded";
    }

    @GetMapping("/search")
    public String searchRecipe(@RequestParam("q") String search, Model model, HttpSession session) throws Exception {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("recipes", recipeService.searchRecipe(search));
        model.addAttribute("query", search);
        model.addAttribute("reportCount", reportService.getReportCount());
        return "community";
    }

    @GetMapping("/recipe/{id}")
    public String getRecipebyId(@PathVariable String id, Model model, HttpSession session) throws Exception {
        Recipe r = recipeService.getRecipeById(id);
        //System.out.println(r);

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("recipe", r);
        model.addAttribute("reportCount", reportService.getReportCount());
        // model.addAttribute("image", Base64.getEncoder().encodeToString(r.getImageData()));
        // model.addAttribute("imageType", r.getImageType());
        return "recipe";
    }

    @GetMapping("/recipe/report/{id}")
    public String reportRecipe(@PathVariable String id, Model model, HttpSession session) throws Exception {
        Recipe r = recipeService.getRecipeById(id);

        //model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("recipe", r);
        model.addAttribute("report", new Report());
        return "reportrecipe";
    }

    @PostMapping("/recipe/report/{id}")
    public String reportRecipePost(@Valid Report report, BindingResult binding, @PathVariable String id, Model model, HttpSession session) throws Exception {
        if (binding.hasErrors()) {
            Recipe r = recipeService.getRecipeById(id);
           // model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("recipe", r);
            return "reportrecipe";
        }
        report.setRecipeId(Integer.valueOf(id));
        reportService.addReport(report);

        //model.addAttribute("username", session.getAttribute("username"));
        return "reported";

    }

}
