package com.guava.parcel.courier.dto.view;

import com.guava.parcel.courier.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CourierView {
    private String lastName;
    private String firstName;
    private Map<Status, Integer> orderStats;
}
