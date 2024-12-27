package com.recipes.FlavourFoundry.controller;

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
import com.recipes.FlavourFoundry.service.ModeratorService;
import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.ReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    ModeratorService moderatorService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    ReportService reportService;

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());
        return "login";
    }


    @PostMapping("/login")
    public String validateLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        try {
            moderatorService.validate(username, password);
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect credentials. Please try again.");
            return "login";
        }

        session.setAttribute("username", username);
        return "redirect:/community";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/community";
    }

    @GetMapping("/recipe/delete/{id}")
    public String deleteRecipebyId(@PathVariable String id, HttpSession session) throws Exception {

        if (session.getAttribute("username") != null) {
            moderatorService.deleteRecipebyId(id);
        }

        return "redirect:/community";
    }

    @GetMapping("/recipe/edit/{id}")
    public String editRecipe(@PathVariable String id, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("username") != null) {
            Recipe r = recipeService.getRecipeById(id);
            System.out.println(moderatorService.unparseList(r.getIngredients(), "\n", false));
            System.out.println(moderatorService.unparseList(r.getInstructions(), "\n", true));
            model.addAttribute("recipe", r);
            model.addAttribute("ingredients", moderatorService.unparseList(r.getIngredients(), "\n", false));
            model.addAttribute("instructions", moderatorService.unparseList(r.getInstructions(), "\n", true));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }

        return "redirect:/community";
    }


    @PostMapping("/recipe/edit/{id}")
    public String addRecipePost(@Valid Recipe recipe,
                                BindingResult binding,
                                @PathVariable String id,
                                @RequestParam("ingredients") String ingredientsStr,
                                @RequestParam("instructions") String instructionsStr, 
                                @RequestParam MultipartFile image, Model model, HttpSession session) throws Exception {
        
        if (binding.hasErrors()) {
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }


        if (ingredientsStr.equals("")) {
            FieldError err = new FieldError("recipe", "ingredients", "Ingredients is mandatory");
            binding.addError(err);
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }

        if (instructionsStr.equals("")) {
            FieldError err = new FieldError("recipe", "instructions", "Instructions is mandatory");
            binding.addError(err);
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }

        try {
            List<String> ingredients = recipeService.parseList(ingredientsStr, "\n", false);
            recipe.setIngredients(ingredients);
        } catch (Exception e) {
            FieldError err = new FieldError("recipe", "ingredients", "Formatting error. Ensure that you type in only one ingredient per line");
            binding.addError(err);
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }

        try {
            List<String> instructions = recipeService.parseList(instructionsStr, "\n", true);
            recipe.setInstructions(instructions);
        } catch (Exception e) {
            FieldError err = new FieldError("recipe", "instructions", "Formatting error. Ensure that you type in only one instruction per line and follow exactly to the numbering format");
            binding.addError(err);
            model.addAttribute("reportCount", reportService.getReportCount());
            return "edit";
        }

        String recipeId = moderatorService.editRecipe(id, recipe, image);

        model.addAttribute("id", recipeId);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());

        return "recipeedited";
    }

    @GetMapping("/reports")
    public String getReports(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            model.addAttribute("reports", reportService.getAllReport());
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "reports";
        }

        return "redirect:/community";
    }

    @GetMapping("/report/{id}")
    public String getReport(@PathVariable String id, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("username") != null) {
            Report r = reportService.getReportById(id);
            model.addAttribute("report", r);
            model.addAttribute("recipe", recipeService.getRecipeById(r.getRecipeId().toString()));
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("reportCount", reportService.getReportCount());
            return "report";
        }

        return "redirect:/community";
    }

    @GetMapping("/report/resolve/{id}")
    public String resolveReport(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            reportService.resolveReportById(id);
            return "redirect:/moderator/reports";
        }

        return "redirect:/community";
    }



}
