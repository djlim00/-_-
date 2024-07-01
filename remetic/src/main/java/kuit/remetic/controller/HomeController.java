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

import java.util.Map;

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
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, Model model) {
        Map<String, Object> attributes = principal.getAttributes();
        System.out.println("Attributes: " + attributes);

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String profileImage = (String) profile.get("profile_image_url");

        System.out.println("Email: " + email);
        System.out.println("Profile Image: " + profileImage);

        User user = userService.findUserByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("email", email);
            model.addAttribute("profileImage", profileImage);
            return "register";
        } else {
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("profileImage", user.getProfileImageUrl());
            return "main";
        }
    }


    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String nickname, @RequestParam String profileImage, Model model) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl(profileImage);
        user.setStatus("active");
        userService.saveUser(user);

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


    @GetMapping("/saveUser")
    public String saveUser(@RequestParam String email, @RequestParam String nickname, @RequestParam String profileImage) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl(profileImage);
        user.setStatus("active");

        userService.saveUser(user);

        return "userSaved";
    }
}