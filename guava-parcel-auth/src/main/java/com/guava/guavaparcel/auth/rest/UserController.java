package com.guava.guavaparcel.auth.rest;

import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.service.api.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("sign-in")
    public Mono<SignInView> signIn(@Valid @RequestBody SignInForm signInForm) {
        return userService.signIn(signInForm);
    }

    @PostMapping("sign-up")
    public Mono<SignUpView> signUp(@Valid @RequestBody SignUpForm signUpForm) {
        return userService.signUp(signUpForm);
    }
}
