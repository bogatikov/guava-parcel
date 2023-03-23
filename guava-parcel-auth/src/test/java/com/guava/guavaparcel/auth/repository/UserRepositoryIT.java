package com.guava.guavaparcel.auth.repository;

import com.guava.guavaparcel.auth.BaseIT;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.repostiory.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

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
                "First",
                "Name",
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
}
