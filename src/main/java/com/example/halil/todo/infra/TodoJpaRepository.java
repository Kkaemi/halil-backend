package com.example.halil.todo.infra;

import com.example.halil.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TodoJpaRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

}
