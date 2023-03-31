package com.guava.guavaparcel.auth.controller;

import com.guava.guavaparcel.auth.dto.form.CreateUserForm;
import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.dto.view.UserView;
import com.guava.guavaparcel.auth.model.Page;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    @PostMapping("create")
    public Mono<UserView> createUser(@Valid @RequestBody CreateUserForm createUserForm) {
        return userService.createUser(createUserForm);
    }

    @GetMapping("list")
    public Mono<Page<UserView>> getUserList(
            @Valid @NotNull @RequestParam("userType") User.UserType userType,
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    ) {
        return userService.getUserList(userType, page,size);
    }
}
