package com.example.halil.user.infra;

import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMemoryRepository implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public Optional<User> findFirstByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    @Override
    public User save(User user) {
        long id = idGenerator.getAndIncrement();
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("엔티티 아이디 리플렉션 실패", e);
        }
        userMap.put(id, user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public void deleteAll() {
        userMap.clear();
    }
}
