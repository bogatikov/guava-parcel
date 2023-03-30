package com.guava.guavaparcel.auth.dto.view;

import com.guava.guavaparcel.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class UserView {
    private String lastName;
    private String firstName;
    private String email;
    private User.UserType userType;
}
