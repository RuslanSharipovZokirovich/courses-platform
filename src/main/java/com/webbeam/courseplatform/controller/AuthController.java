package com.webbeam.courseplatform.controller;

import com.webbeam.courseplatform.model.User;
import com.webbeam.courseplatform.repository.UserRepository;
import com.webbeam.courseplatform.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class AuthController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    private final String masterEmail = "ruslan.sharipov.zokirovich@gmail.com";

    @GetMapping("/login")
    public String loginForm() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/courses";
        }

        return "login";
    }

    @PostMapping("/send-code")
    public String sendCode(@RequestParam String email, HttpSession session, Model model) {
        if (!email.equalsIgnoreCase(masterEmail)) {
            Optional<User> userOpt = userRepository.findByUsername(email);
            if (userOpt.isEmpty() || !userOpt.get().isEnabled()) {
                model.addAttribute("error", "Нет доступа для этого email");
                return "login";
            }
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("authEmail", email);
        session.setAttribute("authCode", code);

        emailService.sendSimpleMessage(email, "Код подтверждения", "Ваш код: " + code);

        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String verifyForm() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/courses";
        }

        return "verify";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String code,
                             HttpSession session,
                             Model model,
                             HttpServletRequest request) {

        String expectedCode = (String) session.getAttribute("authCode");
        String email = (String) session.getAttribute("authEmail");
        String currentIp = request.getRemoteAddr();

        Map<String, String> trustedIps = (Map<String, String>) session.getServletContext().getAttribute("trustedIps");
        if (trustedIps == null) {
            trustedIps = new HashMap<>();
            session.getServletContext().setAttribute("trustedIps", trustedIps);
        }

        if (expectedCode != null && expectedCode.equals(code)) {
            String storedIp = trustedIps.get(email);
            if (storedIp == null) {
                trustedIps.put(email, currentIp);
            } else if (!storedIp.equals(currentIp)) {
                model.addAttribute("error", "Вход с другого устройства запрещён. Обратитесь к администратору.");
                return "verify";
            }

            String role;
            if (email.equalsIgnoreCase(masterEmail)) {
                role = "ROLE_ADMIN";
            } else {
                Optional<User> userOpt = userRepository.findByUsername(email);
                if (userOpt.isEmpty() || !userOpt.get().isEnabled()) {
                    model.addAttribute("error", "Пользователь не найден или отключён");
                    return "verify";
                }
                role = "ROLE_" + userOpt.get().getRole().toUpperCase();
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority(role))
            );

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return role.equals("ROLE_ADMIN") ? "redirect:/admin/panel" : "redirect:/courses";
        } else {
            model.addAttribute("error", "Неверный код");
            return "verify";
        }
    }
}
