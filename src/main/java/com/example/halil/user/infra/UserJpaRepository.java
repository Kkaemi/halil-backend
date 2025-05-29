package com.example.halil.user.infra;

import com.example.halil.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);
}
