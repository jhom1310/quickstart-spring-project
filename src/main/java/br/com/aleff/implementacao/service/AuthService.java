package br.com.aleff.implementacao.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.aleff.implementacao.dto.AuthRequestDTO;
import br.com.aleff.implementacao.dto.AuthResponseDTO;
import br.com.aleff.implementacao.dto.RegisterRequest;
import br.com.aleff.implementacao.dto.TokenRefreshResponseDTO;
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
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);
        return new AuthResponseDTO(new UserResponseDTO(user.getId()), jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        return new AuthResponseDTO(new UserResponseDTO(user.getId()), jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public TokenRefreshResponseDTO refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails); // opcional: renovar também o refresh token

        return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken);
    }
}

