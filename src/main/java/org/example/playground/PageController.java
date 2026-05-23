package org.example.playground;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/index.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/feeds")
    public String feedListPage() {
        return "feed-list";
    }

    @GetMapping("/feeds/new")
    public String feedCreatePage() {
        return "feed-create";
    }

    @GetMapping("/feeds/{feedId}")
    public String feedDetailPage() {
        return "feed-detail";
    }

    @GetMapping("/sha-login")
    public String shaLoginPage() {
        return "sha-login";
    }

    @GetMapping("/sha-signup")
    public String shaSignupPage() {
        return "sha-signup";
    }

    @GetMapping("/unhash-login")
    public String unhashLoginPage() {
        return "unhash-login";
    }

    @GetMapping("/unhash-signup")
    public String unhashSignupPage() {
        return "unhash-signup";
    }

    @GetMapping("/combined-login")
    public String combinedLoginPage() {
        return "combined-login";
    }

    @GetMapping("/combined-signup")
    public String combinedSignupPage() {
        return "combined-signup";
    }
}
