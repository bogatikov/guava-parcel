package com.guava.parcel.admin.ext;

import com.guava.parcel.admin.ext.response.CourierResponse;
import com.guava.parcel.admin.model.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ReactiveFeignClient(name = "${clients.courier-client.name}", url = "${clients.courier-client.url}")
public interface CourierApi {

    @GetMapping("courier/list")
    Mono<Page<CourierResponse>> getCourierList(
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    );
}
