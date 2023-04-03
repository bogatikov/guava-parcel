package com.guava.parcel.user.ext.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SignUpRequest {
    private String lastName;
    private String firstName;
    private String email;
    private String password;
}
