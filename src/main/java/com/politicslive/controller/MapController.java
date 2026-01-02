package com.politicslive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {
    
    @GetMapping("/")
    public String home(){
        return "map"; //templates/map.html
    }
}
