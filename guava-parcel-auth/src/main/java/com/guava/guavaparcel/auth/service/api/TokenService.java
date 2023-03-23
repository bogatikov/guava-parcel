package com.guava.guavaparcel.auth.service.api;

import com.guava.guavaparcel.auth.dto.form.RefreshTokenForm;
import com.guava.guavaparcel.auth.dto.view.TokenView;
import com.guava.guavaparcel.auth.model.User;
import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<TokenView> issueAccessToken(User user);

    Mono<TokenView> issueRefreshToken(User user);

    Mono<TokenView> refreshToken(RefreshTokenForm refreshTokenForm);
}
