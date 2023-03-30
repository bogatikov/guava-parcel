package com.guava.parcel.admin.service;

import com.guava.parcel.admin.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.admin.dto.form.CreateCourierForm;
import com.guava.parcel.admin.dto.form.SetCourierForm;
import com.guava.parcel.admin.dto.form.SignInForm;
import com.guava.parcel.admin.dto.view.CoordinateView;
import com.guava.parcel.admin.dto.view.CourierView;
import com.guava.parcel.admin.dto.view.OrderShortView;
import com.guava.parcel.admin.dto.view.OrderView;
import com.guava.parcel.admin.dto.view.SignInView;
import com.guava.parcel.admin.dto.view.UserView;
import com.guava.parcel.admin.event.CourierCoordinateEvent;
import com.guava.parcel.admin.ext.AuthApi;
import com.guava.parcel.admin.ext.ParcelDeliveryApi;
import com.guava.parcel.admin.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.admin.ext.request.CreateUserRequest;
import com.guava.parcel.admin.ext.request.SetCourierRequest;
import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.OrderResponse;
import com.guava.parcel.admin.ext.response.OrderShortResponse;
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.Status;
import com.guava.parcel.admin.model.UserType;
import com.guava.parcel.admin.service.api.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminService implements AdminService {

    private final ParcelDeliveryApi parcelDeliveryApi;
    private final AuthApi authApi;
    private final ModelMapper mapper;

    private final Sinks.Many<CourierCoordinateEvent> courierCoordinateSink = Sinks.many().multicast().directBestEffort();

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return authApi.signIn(new SignInRequest(signInForm.email(), signInForm.password()))
                .map(signInResponse -> new SignInView(signInResponse.accessToken(), signInResponse.refreshToken()));
    }

    @Override
    public Mono<UserView> createCourier(CreateCourierForm createCourierForm) {
        return Mono.just(createCourierForm)
                .flatMap(form -> authApi.createUser(new CreateUserRequest(form.lastName(), form.firstName(), form.email(), UserType.COURIER)))
                .map(userResponse -> mapper.map(userResponse, UserView.class));
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

    @Override
    public Flux<CoordinateView> subscribeCourierCoordinates(UUID courierId) {
        return courierCoordinateSink.asFlux()
                .filter(courierCoordinateEvent -> courierCoordinateEvent.courierId().equals(courierId))
                .map((event) -> new CoordinateView(
                                courierId,
                                event.longitude(),
                                event.latitude()
                        )
                );
    }

    @Override
    public Mono<Void> consumeCourierCoordinateEvent(CourierCoordinateEvent courierCoordinateEvent) {
        return Mono.fromCallable(() -> courierCoordinateSink.tryEmitNext(courierCoordinateEvent))
                .then();
    }

    private OrderView mapOrderResponseToOrderView(OrderResponse orderResponse) {
        return mapper.map(orderResponse, OrderView.class);
    }

    private OrderShortView mapOrderShortResponseToOrderShortView(OrderShortResponse orderShortResponse) {
        return mapper.map(orderShortResponse, OrderShortView.class);
    }
}
