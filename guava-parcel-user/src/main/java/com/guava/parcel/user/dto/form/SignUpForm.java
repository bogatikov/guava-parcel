package com.guava.parcel.user.dto.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SignUpForm {
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
