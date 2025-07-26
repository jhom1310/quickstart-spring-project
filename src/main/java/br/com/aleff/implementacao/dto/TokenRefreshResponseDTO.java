package br.com.aleff.implementacao.dto;

public class TokenRefreshResponseDTO{
    private String accessToken;
    private String refreshToken;

    public TokenRefreshResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}
