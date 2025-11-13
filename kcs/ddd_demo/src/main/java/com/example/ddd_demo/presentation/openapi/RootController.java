package com.example.ddd_demo.presentation.openapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui/index.html";
    }
}
