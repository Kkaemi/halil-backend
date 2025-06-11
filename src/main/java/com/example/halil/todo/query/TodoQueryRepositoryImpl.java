package com.example.halil.todo.query;

import com.example.halil.todo.domain.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Slice<Todo> findTopSliceBy(Specification<Todo> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Todo> cq = cb.createQuery(Todo.class);
        Root<Todo> root = cq.from(Todo.class);

        // WHERE 절
        Predicate predicate = spec.toPredicate(root, cq, cb);
        if (predicate != null) {
            cq.where(predicate);
        }

        // ORDER BY 절
        List<Order> orders = pageable.getSort().stream()
                .map(o -> o.isAscending()
                        ? cb.asc(root.get(o.getProperty()))
                        : cb.desc(root.get(o.getProperty())))
                .toList();
        cq.orderBy(orders);

        // OFFSET 없이 최신 limit+1건 조회
        TypedQuery<Todo> query = entityManager.createQuery(cq)
                .setMaxResults(pageable.getPageSize() + 1);

        List<Todo> results = query.getResultList();

        // 다음 페이지 유무 판단
        boolean hasNext = results.size() > pageable.getPageSize();
        List<Todo> content = hasNext
                ? results.subList(0, pageable.getPageSize())
                : results;

        // SliceImpl 생성.
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
