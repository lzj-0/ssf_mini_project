package com.recipes.FlavourFoundry.controller;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.ReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ExternalRecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    ReportService reportService;

    @PostMapping("/external")
    public String getRandomRecipe(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession session) {
        String count = form.getFirst("count");
        String include = form.getFirst("include");
        String exclude = form.getFirst("exclude");

        List<Recipe> recipes = recipeService.getRandomRecipe(count, include, exclude);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");

        Calendar cal = Calendar.getInstance(timeZone);
        //System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        model.addAttribute("currHour", cal.get(Calendar.HOUR_OF_DAY));

        model.addAttribute("recipes", recipes);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());

        return "external";

    }
}
