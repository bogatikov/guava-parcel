package com.guava.parcel.delivery.dto.view;

import com.guava.parcel.delivery.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"courierId"})
public class CourierStatsView {
    private UUID courierId;
    private Map<Order.Status, Integer> stats;
}
