package com.guava.parcel.admin.dto.form;

public record CreateCourierForm(
        String lastName,
        String firstName,
        String email
) {
}
