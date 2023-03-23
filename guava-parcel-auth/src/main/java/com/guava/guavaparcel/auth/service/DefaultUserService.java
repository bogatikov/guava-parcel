package com.guava.guavaparcel.auth.service;

import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.error.EntityNotFound;
import com.guava.guavaparcel.auth.error.UserAlreadyExists;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.repostiory.UserRepository;
import com.guava.guavaparcel.auth.service.api.TokenService;
import com.guava.guavaparcel.auth.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return Mono.just(signInForm)
                .flatMap(form -> userRepository.findByEmail(form.email()))
                .filter(user -> user.getPasswordHash().equals(signInForm.password()))
                .switchIfEmpty(Mono.error(new EntityNotFound("User not found")))
                .flatMap(user -> Mono.zip(tokenService.issueAccessToken(user), tokenService.issueRefreshToken(user)))
                .map(tokens -> new SignInView(tokens.getT1().token(), tokens.getT2().token()));
    }

    @Override
    public Mono<SignUpView> signUp(SignUpForm signUpForm) {
        return userRepository.findByEmail(signUpForm.email())
                .flatMap(user -> Mono.error(new UserAlreadyExists(String.format("User with email %s already exists", signUpForm.email()))))
                .switchIfEmpty(userRepository.save(
                                new User(
                                        UUID.randomUUID(),
                                        signUpForm.email(),
                                        signUpForm.lastName(),
                                        signUpForm.firstName(),
                                        signUpForm.password(),
                                        User.UserType.USER,
                                        null,
                                        Instant.now(),
                                        1L,
                                        true
                                )
                        )
                )
                .map(user -> new SignUpView());
    }
}
