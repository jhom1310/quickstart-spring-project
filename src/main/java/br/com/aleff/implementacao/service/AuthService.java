package br.com.aleff.implementacao.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.aleff.implementacao.dto.AuthRequestDTO;
import br.com.aleff.implementacao.dto.AuthResponseDTO;
import br.com.aleff.implementacao.dto.RegisterRequest;
import br.com.aleff.implementacao.dto.TokenRefreshResponseDTO;
import br.com.aleff.implementacao.dto.UpdatePasswordRequestDTO;
import br.com.aleff.implementacao.dto.UserResponseDTO;
import br.com.aleff.implementacao.entity.User;
import br.com.aleff.implementacao.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponseDTO register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);
        return new AuthResponseDTO(new UserResponseDTO(user.getId()), jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
         try {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Usuário inexistente ou senha inválida");
        }
        User user = repository.findByEmail(request.getEmail()).orElseThrow();
        return new AuthResponseDTO(new UserResponseDTO(user.getId()), jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public TokenRefreshResponseDTO refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails); //renovar também o refresh token

        return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken);
    }

    public void updatePassword(UpdatePasswordRequestDTO request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado " + email));

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        if (!request.getNewPassword().equals(request.getNewConfirPassword())) {
            throw new IllegalArgumentException("A confirmação da nova senha não confere");
        }
        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual");
        }

        // Atualizar senha
        user.setPassword(encoder.encode(request.getNewPassword()));
        repository.save(user);
    }


}

