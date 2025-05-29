package com.example.halil.todo.query;

import com.example.halil.todo.domain.Todo;
import org.springframework.data.jpa.domain.Specification;

public class TodoSpecs {

    public static Specification<Todo> hasId(Long id) {
        return (root, query, builder) ->
                id == null ? null : builder.lessThan(root.get("id"), id);
    }

    public static Specification<Todo> hasUserId(Long userId) {
        return (root, query, builder) ->
                userId == null ? null : builder.equal(root.get("author").get("userId"), userId);
    }
}
