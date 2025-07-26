package br.com.aleff.implementacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.aleff.implementacao.dto.AuthRequestDTO;
import br.com.aleff.implementacao.dto.AuthResponseDTO;
import br.com.aleff.implementacao.dto.ForgotPasswordRequestDTO;
import br.com.aleff.implementacao.dto.RegisterRequest;
import br.com.aleff.implementacao.dto.ResetPasswordRequestDTO;
import br.com.aleff.implementacao.dto.TokenRefreshRequestDTO;
import br.com.aleff.implementacao.dto.TokenRefreshResponseDTO;
import br.com.aleff.implementacao.dto.UpdatePasswordRequestDTO;
import br.com.aleff.implementacao.service.AuthService;
import br.com.aleff.implementacao.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(@RequestBody TokenRefreshRequestDTO request) {
        TokenRefreshResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.generatePasswordResetToken(request);
        return ResponseEntity.ok("Email de recuperação enviado se existir conta com este email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO request) {
        authService.updatePassword(request);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }


}