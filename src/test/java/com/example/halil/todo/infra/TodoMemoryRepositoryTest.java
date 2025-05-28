package com.example.halil.todo.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.todo.domain.Author;
import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

class TodoMemoryRepositoryTest {


    @Test
    @DisplayName("엔티티를 저장하면 id 필드가 리플렉션으로 값이 할당 되어야한다")
    void save() {
        // given
        Todo todo = createDummyTodo();
        TodoRepository sut = new TodoMemoryRepository();

        // when
        sut.save(todo);

        // then
        assertThat(todo.getId()).isEqualTo(1L);
    }

    @DisplayName("존재하지 않는 값을 찾으면 빈 Optional을 반환한다")
    @ParameterizedTest
    @CsvSource({
            "5, 3, true",
            "8, 2, true",
            "4, 5, false",
            "3, 0, false"
    })
    void find_by_id(int loop, long idToFind, boolean expected) {
        // given
        TodoRepository sut = new TodoMemoryRepository();

        for (int i = 0; i < loop; i++) {
            Todo todo = createDummyTodo();
            sut.save(todo);
        }

        // when
        Optional<Todo> optional = sut.findById(idToFind);

        // then
        assertThat(optional.isPresent()).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 데이터를 삭제할 경우 아무일도 일어나지 않는다")
    @ParameterizedTest
    @CsvSource({
            // 삭제 성공 후 존재하는 원소 찾음
            "5, 3, 2, true",
            // 삭제 성공 후 존재하지 않는 원소 찾음
            "8, 2, 2, false",
            // 삭제 실패 후 존재하는 원소 찾음
            "4, 5, 3, true",
            // 삭제 실패 후 존재하지 않는 원소 찾음
            "3, 0, 6, false"
    })
    void delete(int loop, long idToDelete, long idToFind, boolean expected) {
        // given
        TodoRepository sut = new TodoMemoryRepository();

        for (int i = 0; i < loop; i++) {
            Todo todo = createDummyTodo();
            sut.save(todo);
        }

        Todo toDelete = createDummyTodo();
        ReflectionTestUtils.setField(toDelete, "id", idToDelete);

        // when
        sut.delete(toDelete);
        Optional<Todo> optional = sut.findById(idToFind);

        // then
        assertThat(optional.isPresent()).isEqualTo(expected);
    }

    private static Todo createDummyTodo() {
        String title = "";
        LocalDateTime createdAt = LocalDateTime.of(2025, 5, 20, 12, 0);
        boolean completed = false;
        Author author = new Author(1L);

        return new Todo(title, createdAt, completed, author);
    }
}
