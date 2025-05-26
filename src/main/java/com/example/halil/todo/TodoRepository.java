package com.example.halil.todo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t JOIN FETCH t.user WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") long todoId);
}
