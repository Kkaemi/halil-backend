package com.example.halil.todo.query;

import com.example.halil.todo.domain.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

public interface TodoQueryRepository {

    Slice<Todo> findTopSliceBy(Specification<Todo> spec, Pageable pageable);
}
