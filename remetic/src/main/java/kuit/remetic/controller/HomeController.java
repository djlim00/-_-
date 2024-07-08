package kuit.remetic.controller;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import kuit.remetic.model.User;
import kuit.remetic.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", ""})
    public String home() {
        return "home";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, Model model, HttpServletResponse response) {
        Map<String, Object> attributes = principal.getAttributes();
        System.out.println("Attributes: " + attributes);

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String profileImage = (String) profile.get("profile_image_url");

        System.out.println("Email: " + email);
        System.out.println("Profile Image: " + profileImage);

        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("email", email);
            model.addAttribute("profileImage", profileImage);
            return "register";
        } else {
            User user = optionalUser.get();
            String token = userService.generateJwtToken(user.getEmail());
            response.setHeader("Authorization", "Bearer " + token);
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("profileImage", user.getProfileImageUrl());
            return "main";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String nickname, @RequestParam String profileImage, Model model, HttpServletResponse response) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl(profileImage);
        user.setStatus("active");
        userService.saveUser(user);

        String token = userService.generateJwtToken(user.getEmail());
        response.setHeader("Authorization", "Bearer " + token);
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("profileImage", user.getProfileImageUrl());
        return "main";
    }

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "loginFailure";
    }
}
