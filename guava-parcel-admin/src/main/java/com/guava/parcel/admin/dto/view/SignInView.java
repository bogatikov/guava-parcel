package com.guava.parcel.admin.dto.view;

public record SignInView(
        String accessToken,
        String refreshToken
) {
}
