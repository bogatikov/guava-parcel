package com.guava.parcel.user.ext;

import com.guava.parcel.user.ext.request.SignInRequest;
import com.guava.parcel.user.ext.request.SignUpRequest;
import com.guava.parcel.user.ext.response.SignInResponse;
import com.guava.parcel.user.ext.response.SignUpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "${clients.auth-client.name}", url = "${clients.auth-client.url}")
public interface AuthApi {

    @PostMapping("user/sign-in")
    Mono<SignInResponse> signIn(@RequestBody SignInRequest signInRequest);

    @PostMapping("user/sign-up")
    Mono<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest);
}
