package com.guava.guavaparcel.auth.controller;

import com.guava.guavaparcel.auth.dto.form.RefreshTokenForm;
import com.guava.guavaparcel.auth.dto.view.TokenView;
import com.guava.guavaparcel.auth.service.api.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("refresh")
    public Mono<TokenView> refreshToken(@Valid @RequestBody RefreshTokenForm refreshTokenForm) {
        return tokenService.refreshToken(refreshTokenForm);
    }
}
