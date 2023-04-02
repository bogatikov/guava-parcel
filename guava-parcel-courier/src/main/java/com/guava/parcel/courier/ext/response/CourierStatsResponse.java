package com.guava.parcel.courier.ext.response;

import com.guava.parcel.courier.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierStatsResponse {
    private UUID courierId;
    private Map<Status, Integer> stats;
}
