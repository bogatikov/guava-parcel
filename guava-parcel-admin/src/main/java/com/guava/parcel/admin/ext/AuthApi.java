package com.guava.parcel.admin.ext;

import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.SignInResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "guava-parcel-auth")
public interface AuthApi {

    @PostMapping("user/sign-in")
    Mono<SignInResponse> signIn(@RequestBody SignInRequest signInRequest);
}
