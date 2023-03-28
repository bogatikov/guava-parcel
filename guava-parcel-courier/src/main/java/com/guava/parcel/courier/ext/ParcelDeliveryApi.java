package com.guava.parcel.courier.ext;


import com.guava.parcel.courier.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.courier.ext.response.OrderResponse;
import com.guava.parcel.courier.ext.response.OrderShortResponse;
import com.guava.parcel.courier.model.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ReactiveFeignClient(name = "${clients.delivery-client.name}", url = "${clients.delivery-client.url}")
public interface ParcelDeliveryApi {

    @PostMapping("order/changeStatus")
    Mono<OrderResponse> changeOrderStatus(@RequestBody ChangeOrderStatusRequest changeOrderStatusForm);

    @GetMapping("order/list")
    Mono<Page<OrderShortResponse>> getOrders(
            @RequestParam(name = "courierId", required = false) UUID courierId,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size
    );

    @GetMapping("order/{orderId}")
    Mono<OrderResponse> getOrder(@PathVariable UUID orderId);
}
