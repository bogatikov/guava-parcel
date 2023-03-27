package com.guava.guavaparcel.repository;

import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.Page;
import com.guava.guavaparcel.model.filter.OrderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultCustomOrderRepository implements CustomOrderRepository {
    private final R2dbcEntityTemplate entityTemplate;
    private final OrderRepository orderRepository;

    @Override
    public Mono<Page<Order>> getOrdersByFilter(OrderFilter orderFilter, Integer page, Integer size) {
        Query query = buildQuery(orderFilter);
        return entityTemplate.select(query.with(PageRequest.of(page, size)), Order.class)
                .collectList()
                .zipWith(entityTemplate.count(query, Order.class))
                .map(tuple -> new Page<>(tuple.getT1(), page, tuple.getT2(), tuple.getT1().size()));
    }

    @Override
    public OrderRepository getOriginal() {
        return orderRepository;
    }

    private Query buildQuery(OrderFilter orderFilter) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (orderFilter.courierId() != null) {
            criteriaList.add(Criteria.where("courier_id").is(orderFilter.courierId()));
        }
        if (orderFilter.userId() != null) {
            criteriaList.add(Criteria.where("user_id").is(orderFilter.userId()));
        }
        if (orderFilter.status() != null) {
            criteriaList.add(Criteria.where("status").is(orderFilter.status()));
        }
        return Query.query(CriteriaDefinition.from(criteriaList));
    }
}
