package com.recipes.FlavourFoundry.controller;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.ReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = {"/", "index"})
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    ReportService reportService;
    
    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/external")
    public String external(Model model, HttpSession session) {

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");

        Calendar cal = Calendar.getInstance(timeZone);
        model.addAttribute("currHour", cal.get(Calendar.HOUR_OF_DAY));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());
        //System.out.println("Hour:" + cal.get(Calendar.HOUR_OF_DAY));
        return "external";
    }

    @GetMapping("/community")
    public String community(Model model, HttpSession session) throws Exception {

        model.addAttribute("recipes", recipeService.getAllRecipe());
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reportCount", reportService.getReportCount());
        
        return "community";
    }

}
