package com.guava.parcel.delivery.repository;

import com.guava.parcel.delivery.model.Order;
import com.guava.parcel.delivery.model.Page;
import com.guava.parcel.delivery.model.filter.OrderFilter;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DefaultCustomOrderRepository implements CustomOrderRepository {
    private final R2dbcEntityTemplate entityTemplate;

    private static final String STATS_QUERY = "select o.status, count(*) as cnt from orders o where o.courier_id = :courierId group by status;";

    @Override
    public Mono<Page<Order>> getOrdersByFilter(OrderFilter orderFilter, Integer page, Integer size) {
        Query query = buildQuery(orderFilter);
        return entityTemplate.select(query.with(PageRequest.of(page, size)), Order.class)
                .collectList()
                .zipWith(entityTemplate.count(query, Order.class))
                .map(tuple -> new Page<>(tuple.getT1(), page, tuple.getT2(), tuple.getT1().size()));
    }

    @Override
    public Mono<Map<Order.Status, Integer>> getCourierStatsByCourierId(UUID courierId) {
        return entityTemplate.getDatabaseClient()
                .sql(STATS_QUERY)
                .bind("courierId", courierId)
                .map(row -> new OrderStatsProjection(
                        Order.Status.valueOf(Objects.requireNonNull(row.get(0)).toString()),
                        row.get(1, Integer.class))
                )
                .all()
                .collectList()
                .map(stats -> stats.stream().collect(Collectors.toMap(OrderStatsProjection::getStatus, OrderStatsProjection::getCnt)));
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
