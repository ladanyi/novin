package com.example.invoice_app.controller;

import com.example.invoice_app.model.AppUser;
import com.example.invoice_app.model.Role;
import com.example.invoice_app.payload.request.LoginRequest;
import com.example.invoice_app.payload.request.SignupRequest;
import com.example.invoice_app.payload.response.JwtResponse;
import com.example.invoice_app.payload.response.MessageResponse;
import com.example.invoice_app.security.jwt.JwtUtils;
import com.example.invoice_app.service.RoleService;
import com.example.invoice_app.service.UserService;
import com.example.invoice_app.util.RecaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RecaptchaUtil recaptchaUtil;

    @Value("${login.max.attempts}")
    private int maxLoginAttempts;

    private final Map<String, Integer> loginAttemptsCache = new HashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        AppUser user = new AppUser();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(signUpRequest.getPassword());

        Role userRole = roleService.findByName(signUpRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(userRole));

        userService.saveUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();

        if (isBlocked(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Too many failed login attempts. Please try again later."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            AppUser userDetails = (AppUser) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            resetAttempts(username);

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
        } catch (BadCredentialsException e) {
            incrementAttempts(username);
            if (getAttempts(username) >= maxLoginAttempts) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Too many failed login attempts. Please complete the reCAPTCHA."));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid username or password."));
        }
    }

    private void incrementAttempts(String username) {
        loginAttemptsCache.put(username, loginAttemptsCache.getOrDefault(username, 0) + 1);
    }

    private void resetAttempts(String username) {
        loginAttemptsCache.remove(username);
    }

    private boolean isBlocked(String username) {
        return loginAttemptsCache.getOrDefault(username, 0) >= maxLoginAttempts;
    }

    private int getAttempts(String username) {
        return loginAttemptsCache.getOrDefault(username, 0);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}
