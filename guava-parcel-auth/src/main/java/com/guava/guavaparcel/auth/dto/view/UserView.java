package com.guava.guavaparcel.auth.dto.view;

import com.guava.guavaparcel.auth.model.User;

public record UserView(
        String lastName,
        String firstName,
        String email,
        User.UserType userType
) {
}
