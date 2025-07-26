package br.com.aleff.implementacao.dto;
import lombok.Data;

@Data
public class TokenRefreshResponseDTO{
    private final String accessToken;
    private final String refreshToken;
}
