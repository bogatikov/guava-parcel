package com.guava.parcel.admin.ext;

import com.guava.parcel.admin.ext.request.CreateUserRequest;
import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.SignInResponse;
import com.guava.parcel.admin.ext.response.UserResponse;
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.UserType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "${clients.auth-client.name}", url = "${clients.auth-client.url}")
public interface AuthApi {

    @PostMapping("user/sign-in")
    Mono<SignInResponse> signIn(@RequestBody SignInRequest signInRequest);

    @PostMapping("user/createUser")
    Mono<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest);

    @GetMapping("user/list")
    Mono<Page<UserResponse>> getUserList(
            @RequestParam() UserType userType,
            @RequestParam() Integer page,
            @RequestParam() Integer size
    );
}
