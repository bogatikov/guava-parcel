package com.guava.parcel.courier.ext.response;

import com.guava.parcel.courier.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponse {
    private UUID id;
    private String lastName;
    private String firstName;
    private String email;
    private UserType userType;
}
