package com.guava.guavaparcel.auth.repository;

import com.guava.guavaparcel.auth.BaseIT;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.repostiory.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserRepositoryIT extends BaseIT {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test userRepository.findByEmail()")
    public void testFindByEmail() {
        String email = "my@email.com";
        var user = new User(
                UUID.randomUUID(),
                email,
                "John",
                "Doe",
                "password",
                User.UserType.USER,
                Instant.now(),
                Instant.now(),
                1L,
                true
        );

        userRepository.save(user).block();

        StepVerifier.create(userRepository.findByEmail(email))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test userRepository.existsByEmail()")
    public void testExistsByEmailWhenThereIsNoEntryWithEmail() {
        String email = "my@email.com";

        StepVerifier.create(userRepository.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test userRepository.existsByEmail()")
    public void testExistsByEmailWhenThereIsEntryWithEmail() {
        String email = "my@email.com";
        var user = new User(
                UUID.randomUUID(),
                email,
                "John",
                "Doe",
                "password",
                User.UserType.USER,
                Instant.now(),
                Instant.now(),
                1L,
                true
        );

        userRepository.save(user).block();

        StepVerifier.create(userRepository.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test userRepository.countByType()")
    public void testCountByType() {
        String email = "my@email.com";
        var user = new User(
                UUID.randomUUID(),
                email,
                "John",
                "Doe",
                "password",
                User.UserType.USER,
                Instant.now(),
                Instant.now(),
                1L,
                true
        );

        userRepository.save(user).block();

        StepVerifier.create(userRepository.countAllByUserType(User.UserType.USER))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userRepository.countAllByUserType(User.UserType.ADMIN))
                .expectNext(0L)
                .verifyComplete();

        StepVerifier.create(userRepository.countAllByUserType(User.UserType.COURIER))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test userRepository.findAllByType()")
    public void testFindAllByType() {
        String email = "gray@dorian.com";
        var user1 = new User(
                UUID.randomUUID(),
                email,
                "Gray",
                "Dorian",
                "password",
                User.UserType.USER,
                Instant.now(),
                Instant.now(),
                1L,
                true
        );
        var user2 = new User(
                UUID.randomUUID(),
                "john@doe.com",
                "Doe",
                "John",
                "password",
                User.UserType.ADMIN,
                Instant.now(),
                Instant.now(),
                1L,
                true
        );

        userRepository.saveAll(List.of(user1, user2)).blockLast();

        StepVerifier.create(userRepository.findAllByUserType(User.UserType.USER, Pageable.unpaged()))
                .assertNext(user -> assertEquals(user.getId(), user1.getId()))
                .verifyComplete();
    }
}
