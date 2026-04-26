package com.personalassistant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() { return "forward:/login.html"; }

    @GetMapping("/register")
    public String register() { return "forward:/register.html"; }

    @GetMapping("/forgot-password")
    public String forgotPassword() { return "forward:/forgot-password.html"; }
}
