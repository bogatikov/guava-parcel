package com.guava.parcel.user.ext.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SignInResponse {
    private String accessToken;
    private String refreshToken;
}
