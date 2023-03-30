package com.guava.guavaparcel.auth.service;

import com.guava.guavaparcel.auth.dto.form.CreateUserForm;
import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.dto.view.UserView;
import com.guava.guavaparcel.auth.error.EntityNotFound;
import com.guava.guavaparcel.auth.error.UserAlreadyExists;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.repostiory.UserRepository;
import com.guava.guavaparcel.auth.service.api.TokenService;
import com.guava.guavaparcel.auth.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private static final Integer DEFAULT_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ModelMapper mapper;

    @Override
    public Mono<UserView> createUser(CreateUserForm createUserForm) {
        return Mono.just(createUserForm)
                .filterWhen(form -> userRepository.existsByEmail(form.email())
                        .map(exists -> !exists)
                )
                .switchIfEmpty(Mono.error(new UserAlreadyExists("User with email %s already exists".formatted(createUserForm.email()))))
                .map(form -> new User(UUID.randomUUID(),
                        form.email(),
                        form.lastName(),
                        form.firstName(),
                        generatePassword(),
                        createUserForm.userType(),
                        null,
                        Instant.now(),
                        1L,
                        true))
                .flatMap(userRepository::save)
                .map(savedUser -> mapper.map(savedUser, UserView.class));
    }

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

    private String generatePassword() {
        return RandomStringUtils.randomAlphabetic(DEFAULT_PASSWORD_LENGTH);
    }
}
