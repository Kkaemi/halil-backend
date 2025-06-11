package com.example.halil.todo.infra;

import com.example.halil.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoJpaRepository extends JpaRepository<Todo, Long> {

}
