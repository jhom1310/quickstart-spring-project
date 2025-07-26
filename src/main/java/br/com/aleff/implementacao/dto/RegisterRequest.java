package br.com.aleff.implementacao.dto;

import br.com.aleff.implementacao.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}