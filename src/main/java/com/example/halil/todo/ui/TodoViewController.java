package com.example.halil.todo.ui;

import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.query.TodoQueryRepository;
import com.example.halil.todo.query.TodoSpecs;
import com.example.halil.todo.query.TodoView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoViewController {

    private final TodoQueryRepository todoQueryRepository;

    @GetMapping("/v1/todos")
    public Slice<TodoView> findTodo(
            @AuthenticationPrincipal long userId,
            @RequestParam(name = "last_id", required = false) Long todoIdOrNull,
            @PageableDefault(size = 20, sort = {"id"}, direction = Direction.DESC) Pageable pageable
    ) {
        Specification<Todo> spec = Specification
                .where(TodoSpecs.hasUserId(userId))
                .and(TodoSpecs.hasId(todoIdOrNull));

        return todoQueryRepository.findTopSliceBy(spec, pageable)
                .map(TodoView::of);
    }
}
