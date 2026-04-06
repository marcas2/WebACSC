package com.tuapp.presentation.controller;

import com.tuapp.application.dto.AuthResponse;
import com.tuapp.application.dto.LoginRequest;
import com.tuapp.application.dto.RegisterRequest;
import com.tuapp.application.usecase.LoginUseCase;
import com.tuapp.application.usecase.RegisterUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest request,
                        BindingResult bindingResult,
                        HttpServletResponse response,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            AuthResponse authResponse = loginUseCase.execute(request);

            Cookie cookie = new Cookie("jwt", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true en producción con HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

@PostMapping("/register")
public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                       BindingResult bindingResult,
                       Model model) {
    if (bindingResult.hasErrors()) {
        return "register";
    }

    try {
        registerUseCase.execute(request);
        return "redirect:/auth/login?registered=true";

    } catch (IllegalArgumentException e) {
        model.addAttribute("error", e.getMessage());
        return "register";

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Ocurrió un error interno al registrar el usuario.");
        return "register";
    }
}

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/auth/login?logout=true";
    }
}