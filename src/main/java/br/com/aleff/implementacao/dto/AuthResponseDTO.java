package br.com.aleff.implementacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private UserResponseDTO user;
    private String token;
    private String refreshToken;
}
