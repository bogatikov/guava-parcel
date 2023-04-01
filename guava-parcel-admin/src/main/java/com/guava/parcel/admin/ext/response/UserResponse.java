package com.guava.parcel.admin.ext.response;

import com.guava.parcel.admin.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponse {
    private String lastName;
    private String firstName;
    private String email;
    private UserType userType;
}
