package br.com.aleff.implementacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/user/me")
    public ResponseEntity<String> me(Authentication auth) {
        return ResponseEntity.ok("Olá, " + auth.getName());
    }

    @GetMapping("/admin/only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Área ADMIN");
    }
}