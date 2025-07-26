package br.com.aleff.implementacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aleff.implementacao.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}