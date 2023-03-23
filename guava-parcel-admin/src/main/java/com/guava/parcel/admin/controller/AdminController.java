package com.guava.parcel.admin.controller;

import com.guava.parcel.admin.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.admin.dto.form.CreateCourierForm;
import com.guava.parcel.admin.dto.view.CreateCourierView;
import com.guava.parcel.admin.dto.view.OrderShortView;
import com.guava.parcel.admin.dto.view.OrderView;
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.Status;
import com.guava.parcel.admin.service.api.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final AdminService adminService;

    @GetMapping("order/list")
    public Mono<Page<OrderShortView>> getOrders(
            @RequestParam(value = "status", required = false) Status status,
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    ) {
        return adminService.getOrders(status, page, size);
    }


    @GetMapping("order/{orderId}")
    public Mono<OrderView> getOrder(@Valid @NonNull @PathVariable UUID orderId) {
        return adminService.getOrder(orderId);
    }

    @PostMapping("order/changeStatus")
    public Mono<OrderView> changeOrderStatus(@Valid @RequestBody ChangeOrderStatusForm changeOrderStatusForm) {
        return adminService.changeOrderStatus(changeOrderStatusForm);
    }

    @PostMapping("courier/create")
    public Mono<CreateCourierView> createCourier(@Valid @RequestBody CreateCourierForm createCourierForm) {
        return adminService.createCourier(createCourierForm);
    }
}
