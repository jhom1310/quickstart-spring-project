package br.com.aleff.implementacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {

    @NotBlank(message = "A senha atual é obrigatória")
    private String currentPassword;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
    private String newPassword;

    @NotBlank(message = "Confirmar senha é obrigatório")
    @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
    private String newConfirPassword;
}
