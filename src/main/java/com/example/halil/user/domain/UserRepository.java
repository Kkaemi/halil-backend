package com.example.halil.user.domain;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findFirstByEmail(String email);

    User save(User user);

    Optional<User> findById(Long userId);

    void deleteAll();
}
