package com.guava.parcel.delivery.repository;

import com.guava.parcel.delivery.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderStatsProjection {
    private Order.Status status;
    private Integer cnt;
}