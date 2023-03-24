package com.guava.parcel.admin.service;

import com.guava.parcel.admin.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.admin.dto.form.CreateCourierForm;
import com.guava.parcel.admin.dto.form.SetCourierForm;
import com.guava.parcel.admin.dto.form.SignInForm;
import com.guava.parcel.admin.dto.view.CoordinateView;
import com.guava.parcel.admin.dto.view.CourierView;
import com.guava.parcel.admin.dto.view.CreateCourierView;
import com.guava.parcel.admin.dto.view.OrderShortView;
import com.guava.parcel.admin.dto.view.OrderView;
import com.guava.parcel.admin.dto.view.SignInView;
import com.guava.parcel.admin.ext.AuthApi;
import com.guava.parcel.admin.ext.ParcelDeliveryApi;
import com.guava.parcel.admin.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.admin.ext.request.SetCourierRequest;
import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.OrderResponse;
import com.guava.parcel.admin.ext.response.OrderShortResponse;
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.Status;
import com.guava.parcel.admin.service.api.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {

    private final ParcelDeliveryApi parcelDeliveryApi;
    private final AuthApi authApi;
    private final ModelMapper mapper;

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return authApi.signIn(new SignInRequest(signInForm.email(), signInForm.password()))
                .map(signInResponse -> new SignInView(signInResponse.accessToken(), signInResponse.refreshToken()));
    }

    @Override
    public Mono<CreateCourierView> createCourier(CreateCourierForm createCourierForm) {
        //todo
        return null;
    }

    @Override
    public Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm) {
        return parcelDeliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(changeOrderStatusForm.orderId(), changeOrderStatusForm.status()))
                .map(this::mapOrderResponseToOrderView);
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders(Status status, @NonNull Integer page, @NonNull Integer size) {
        return parcelDeliveryApi.getOrders(status, page, size)
                .map(contentPage -> new Page<>(
                                contentPage.getContent().stream()
                                        .map(this::mapOrderShortResponseToOrderShortView)
                                        .collect(Collectors.toList()),
                                contentPage.getCurrentPage(),
                                contentPage.getTotalElements(),
                                contentPage.getNumberOfElements()
                        )
                );
    }

    @Override
    public Mono<OrderView> setCourier(SetCourierForm setCourierForm) {
        return parcelDeliveryApi.setCourier(new SetCourierRequest(setCourierForm.orderId(), setCourierForm.courierId()))
                .map(this::mapOrderResponseToOrderView);
    }

    @Override
    public Flux<CoordinateView> getCourierCoordinates(UUID courierId) {
        return null;
    }

    @Override
    public Mono<Page<CourierView>> getCouriers() {
        return null;
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return parcelDeliveryApi.getOrder(orderId)
                .map(this::mapOrderResponseToOrderView);
    }

    private OrderView mapOrderResponseToOrderView(OrderResponse orderResponse) {
        return mapper.map(orderResponse, OrderView.class);
    }

    private OrderShortView mapOrderShortResponseToOrderShortView(OrderShortResponse orderShortResponse) {
        return mapper.map(orderShortResponse, OrderShortView.class);
    }
}
