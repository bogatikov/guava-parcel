package com.guava.guavaparcel.auth.dto.view;

import com.guava.guavaparcel.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class UserView {
    private UUID id;
    private String lastName;
    private String firstName;
    private String email;
    private User.UserType userType;
}
