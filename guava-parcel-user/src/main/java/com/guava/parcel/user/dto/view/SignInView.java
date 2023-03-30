package com.guava.parcel.user.dto.view;

public record SignInView(
        String accessToken,
        String refreshToken
) {
}
