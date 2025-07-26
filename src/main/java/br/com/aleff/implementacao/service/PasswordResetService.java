package br.com.aleff.implementacao.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.aleff.implementacao.dto.ForgotPasswordRequestDTO;
import br.com.aleff.implementacao.dto.ResetPasswordRequestDTO;
import br.com.aleff.implementacao.entity.PasswordResetToken;
import br.com.aleff.implementacao.entity.User;
import br.com.aleff.implementacao.repository.PasswordResetTokenRepository;
import br.com.aleff.implementacao.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 15 minutos de validade (exemplo)
    private static final long EXP_MINUTES = 15;

    public void generatePasswordResetToken(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken prt = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(Instant.now().plusSeconds(EXP_MINUTES * 60))
                    .used(false)
                    .build();
            tokenRepository.save(prt);

            // TODO: enviar e-mail com o link contendo o token
            // ex: https://seu-front.com/reset-password?token=" + token
        });
        // Por segurança, não revelar se o email existe ou não
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetToken prt = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (prt.isUsed() || prt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expirado ou já utilizado");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
    }
}
