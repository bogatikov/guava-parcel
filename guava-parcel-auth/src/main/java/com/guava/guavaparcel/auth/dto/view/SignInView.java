package com.guava.guavaparcel.auth.dto.view;

public record SignInView(
        String accessToken,
        String refreshToken
) {
}
