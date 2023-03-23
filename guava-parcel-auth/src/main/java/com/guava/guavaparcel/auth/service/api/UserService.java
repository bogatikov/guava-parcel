package com.guava.guavaparcel.auth.service.api;

import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<SignInView> signIn(SignInForm signInForm);

    Mono<SignUpView> signUp(SignUpForm signUpForm);
}
