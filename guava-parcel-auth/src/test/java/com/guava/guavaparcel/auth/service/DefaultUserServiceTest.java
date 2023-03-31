package com.guava.guavaparcel.auth.service;

import com.guava.guavaparcel.auth.dto.form.CreateUserForm;
import com.guava.guavaparcel.auth.dto.form.SignInForm;
import com.guava.guavaparcel.auth.dto.form.SignUpForm;
import com.guava.guavaparcel.auth.dto.view.SignInView;
import com.guava.guavaparcel.auth.dto.view.SignUpView;
import com.guava.guavaparcel.auth.dto.view.TokenView;
import com.guava.guavaparcel.auth.dto.view.UserView;
import com.guava.guavaparcel.auth.error.EntityNotFound;
import com.guava.guavaparcel.auth.error.UserAlreadyExists;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.repostiory.UserRepository;
import com.guava.guavaparcel.auth.service.api.TokenService;
import com.guava.guavaparcel.auth.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenService = mock(TokenService.class);
        userService = new DefaultUserService(userRepository, tokenService, new ModelMapper());
    }

    @Test
    void signInShouldThrowExceptionWhenUserNotExists() {
        String email = "my@email.ru";
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());
        StepVerifier.create(userService.signIn(new SignInForm(email, "password")))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void signInShouldThrowExceptionWhenIncorrectPassword() {
        String email = "my@email.ru";
        var user = new User(
                UUID.randomUUID(),
                email,
                "Doe",
                "John",
                "secretPassword",
                User.UserType.USER,
                null,
                Instant.now(),
                1L,
                false
        );
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        StepVerifier.create(userService.signIn(new SignInForm(email, "password")))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void signInShouldReturnTokens() {
        String email = "my@email.ru";
        var user = new User(
                UUID.randomUUID(),
                email,
                "Doe",
                "John",
                "password",
                User.UserType.USER,
                null,
                Instant.now(),
                1L,
                false
        );

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(tokenService.issueAccessToken(user)).thenReturn(Mono.just(new TokenView("access-token")));
        when(tokenService.issueRefreshToken(user)).thenReturn(Mono.just(new TokenView("refresh-token")));

        StepVerifier.create(userService.signIn(new SignInForm(email, "password")))
                .expectNext(new SignInView("access-token", "refresh-token"))
                .verifyComplete();

        verify(tokenService, times(1)).issueAccessToken(user);
        verify(tokenService, times(1)).issueRefreshToken(user);
    }

    @Test
    void shouldRaiseUserAlreadyExistsWhenTryingToSignUpWithExistingEmail() {
        String email = "my@email.ru";
        var user = new User(
                UUID.randomUUID(),
                email,
                "Doe",
                "John",
                "password",
                User.UserType.USER,
                null,
                Instant.now(),
                1L,
                false
        );

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(userRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(userService.signUp(new SignUpForm("Dorian", "Gray", "my@email.ru", "secret")))
                .verifyError(UserAlreadyExists.class);
    }

    @Test
    void shouldSaveUser() {
        String email = "my@email.ru";

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());
        when(userRepository.save(any())).thenAnswer((answer) -> Mono.just(answer.getArgument(0)));

        StepVerifier.create(userService.signUp(new SignUpForm("Dorian", "Gray", email, "secret")))
                .expectNext(new SignUpView())
                .verifyComplete();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        assertEquals("Gray", userCaptor.getValue().getFirstName());
        assertEquals("Dorian", userCaptor.getValue().getLastName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals("secret", userCaptor.getValue().getPasswordHash());
    }

    @Test
    void createUserShouldSaveUser() {
        String email = "my@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        when(userRepository.save(any())).thenAnswer(answer -> Mono.just(answer.getArgument(0)));

        StepVerifier.create(userService.createUser(new CreateUserForm("Doe", "John", email, User.UserType.USER)))
                .assertNext(userView -> {
                    assertEquals("Doe", userView.getLastName());
                    assertEquals("John", userView.getFirstName());
                    assertEquals(email, userView.getEmail());
                    assertEquals(User.UserType.USER, userView.getUserType());
                })
                .verifyComplete();

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUserShouldRaiseUserAlreadyExists() {
        String email = "my@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(true));

        StepVerifier.create(userService.createUser(new CreateUserForm("Doe", "John", email, User.UserType.USER)))
                .verifyError(UserAlreadyExists.class);

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void getUserListEmpty() {
        when(userRepository.findAllByUserType(User.UserType.USER, PageRequest.of(0, 20)))
                .thenReturn(Flux.empty());
        when(userRepository.countAllByUserType(User.UserType.USER))
                .thenReturn(Mono.just(0L));

        StepVerifier.create(userService.getUserList(User.UserType.USER, 0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(0, page.getNumberOfElements());
                    assertEquals(0, page.getTotalElements());
                    assertEquals(0, page.getContent().size());
                })
                .verifyComplete();
    }

    @Test
    void getUserList() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        when(userRepository.findAllByUserType(User.UserType.USER, PageRequest.of(0, 20)))
                .thenReturn(Flux.just(new User(
                                userId1,
                                "dorian@gray.com",
                                "Gray",
                                "Dorian",
                                "password",
                                User.UserType.USER,
                                Instant.now(),
                                Instant.now(),
                                1L,
                                true
                        ),
                        new User(
                                userId2,
                                "john@doe.com",
                                "Doe",
                                "John",
                                "password",
                                User.UserType.ADMIN,
                                Instant.now(),
                                Instant.now(),
                                1L,
                                true
                        )));
        when(userRepository.countAllByUserType(User.UserType.USER))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(userService.getUserList(User.UserType.USER, 0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(2, page.getNumberOfElements());
                    assertEquals(2, page.getTotalElements());
                    assertEquals(2, page.getContent().size());
                    var emails = page.getContent().stream().map(UserView::getEmail).toList();
                    assertTrue(emails.containsAll(List.of("dorian@gray.com", "john@doe.com")));
                })
                .verifyComplete();
    }
}