package com.example.halil.todo.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.halil.todo.domain.Author;
import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import com.example.halil.todo.dto.TodoUpdateRequestDto;
import com.example.halil.todo.infra.TodoMemoryRepository;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TodoServiceTest {

    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = new TodoMemoryRepository();

        LocalDateTime now = LocalDateTime.now();
        boolean completed = false;

        for (long i = 1; i <= 5; i++) {
            String title = "todo" + i;
            Author author = new Author(i);
            Todo todo = new Todo(title, now, completed, author);
            todoRepository.save(todo);
        }
    }

    @ParameterizedTest
    @DisplayName("조회 할 수 없으면 예외 발생 & 본인이 작성한 할 일이 아니면 수정 불가")
    @MethodSource("provideTodoIdAndAuthorAndErrorCode")
    void others_can_not_update(long todoId, Author editor, TodoErrorCode errorCode) {
        // given
        TodoUpdateRequestDto dto = new TodoUpdateRequestDto();

        TodoService sut = new TodoService(todoRepository);

        // when and then
        assertThatThrownBy(() -> sut.update(todoId, editor, dto))
                .isInstanceOf(errorCode.exception().getClass());
    }

    @ParameterizedTest
    @DisplayName("조회 할 수 없으면 예외 발생 & 본인이 작성한 할 일이 아니면 삭제 불가")
    @MethodSource("provideTodoIdAndAuthorAndErrorCode")
    void others_can_not_delete(long todoId, Author author, TodoErrorCode errorCode) {
        // given
        TodoService sut = new TodoService(todoRepository);

        // when and then
        assertThatThrownBy(() -> sut.delete(todoId, author))
                .isInstanceOf(errorCode.exception().getClass());
    }

    private static Stream<Arguments> provideTodoIdAndAuthorAndErrorCode() {
        return Stream.of(
                Arguments.of(1L, new Author(2L), TodoErrorCode.ACCESS_DENIED),
                Arguments.of(0L, new Author(2L), TodoErrorCode.TODO_NOT_FOUND)
        );
    }
}
