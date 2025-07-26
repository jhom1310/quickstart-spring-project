package br.com.aleff.implementacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;
}
