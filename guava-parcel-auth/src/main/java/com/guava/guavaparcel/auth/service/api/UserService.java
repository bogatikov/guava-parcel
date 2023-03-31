package com.guava.guavaparcel.auth.service.api;

import com.guava.guavaparcel.auth.dto.form.CreateUserForm;
import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.dto.view.UserView;
import com.guava.guavaparcel.auth.model.Page;
import com.guava.guavaparcel.auth.model.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserView> createUser(CreateUserForm createUserForm);

    Mono<SignInView> signIn(SignInForm signInForm);

    Mono<SignUpView> signUp(SignUpForm signUpForm);

    Mono<Page<UserView>> getUserList(User.UserType userType, Integer page, Integer size);
}
