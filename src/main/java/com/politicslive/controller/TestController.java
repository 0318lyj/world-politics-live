package com.politicslive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.politicslive.service.GdeltService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final GdeltService gdeltService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("heatPoints", gdeltService.getRecentHeatPoints());
        return "map";
    }
}
