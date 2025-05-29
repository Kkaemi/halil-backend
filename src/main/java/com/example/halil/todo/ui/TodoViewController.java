package com.example.halil.todo.ui;

import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.infra.TodoJpaRepository;
import com.example.halil.todo.query.TodoSpecs;
import com.example.halil.todo.query.TodoView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoViewController {

    private final TodoJpaRepository todoJpaRepository;

    @GetMapping("/v1/todos")
    public List<TodoView> findTodo(
            @AuthenticationPrincipal long userId,
            @RequestParam(name = "last_id", required = false) Long todoIdOrNull,
            @SortDefault(sort = {"id"}, direction = Direction.DESC) Sort sort
    ) {
        Specification<Todo> spec = Specification
                .where(TodoSpecs.hasUserId(userId))
                .and(TodoSpecs.hasId(todoIdOrNull));

        return todoJpaRepository.findAll(spec, sort)
                .stream().map(TodoView::of).toList();
    }
}
