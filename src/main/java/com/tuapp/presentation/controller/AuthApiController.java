package com.tuapp.presentation.controller;

import com.tuapp.application.dto.AuthResponse;
import com.tuapp.application.dto.LoginRequest;
import com.tuapp.application.dto.RegisterRequest;
import com.tuapp.application.usecase.LoginUseCase;
import com.tuapp.application.usecase.RegisterUseCase;
import com.tuapp.domain.model.User;
import com.tuapp.presentation.dto.LoginApiRequest;
import com.tuapp.presentation.dto.RegisterApiRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthApiController(LoginUseCase loginUseCase,
                             RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginFromApp(
            @Valid @RequestBody LoginApiRequest request) {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getNombreUsuario());
        loginRequest.setPassword(request.getContrasena());

        try {
            AuthResponse authResponse = loginUseCase.execute(loginRequest);

            Map<String, Object> user = new LinkedHashMap<>();
            user.put("id", authResponse.getUserId());
            user.put("nombreUsuario", authResponse.getUsername());
            user.put("email", authResponse.getEmail());
            user.put("rol", authResponse.getRole());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Login exitoso");
            response.put("token", authResponse.getToken());
            response.put("usuario", user);

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerFromMobile(
            @Valid @RequestBody RegisterApiRequest request) {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(request.getNombreUsuario());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getContrasena());

        User created = registerUseCase.execute(registerRequest);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Usuario creado correctamente");
        response.put("id", created.getId());
        response.put("nombreUsuario", created.getNombreUsuario());
        response.put("email", created.getEmail());
        response.put("rol", created.getRoleName());
        response.put("creadoEn", created.getCreadoEn());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessError(IllegalArgumentException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Datos invalidos");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}