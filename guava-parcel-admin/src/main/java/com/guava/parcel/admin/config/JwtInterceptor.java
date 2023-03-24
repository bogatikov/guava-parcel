package com.guava.parcel.admin.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtInterceptor implements ReactiveHttpRequestInterceptor {

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest reactiveHttpRequest) {
        if (isUnsecureRequest(reactiveHttpRequest)) {
            return Mono.just(reactiveHttpRequest);
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    var credentials = (String) authentication.getCredentials();
                    reactiveHttpRequest.headers().put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + credentials));
                    return reactiveHttpRequest;
                });
    }

    private boolean isUnsecureRequest(ReactiveHttpRequest reactiveHttpRequest) {
        return reactiveHttpRequest.uri().getPath().equals("/user/sign-in");
    }
}