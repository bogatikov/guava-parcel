package com.guava.parcel.admin.dto.view;

import com.guava.parcel.admin.model.UserType;
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
    private UserType userType;
}
