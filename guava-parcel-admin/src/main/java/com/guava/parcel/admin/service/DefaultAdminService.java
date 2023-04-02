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
import com.guava.parcel.admin.ext.CourierApi;
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
import com.guava.parcel.admin.stream.CourierCoordinateStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminService implements AdminService {

    private final CourierApi courierApi;
    private final ParcelDeliveryApi parcelDeliveryApi;
    private final AuthApi authApi;
    private final ModelMapper mapper;
    private final CourierCoordinateStream coordinateStream;


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
    public Mono<Page<CourierView>> getCouriers(Integer page, Integer size) {
        // TODO: 02.04.2023 get courier list
        return courierApi.getCourierList(page, size)
                .map(userResponsePage -> {
                    List<CourierView> courierViewList = userResponsePage.getContent().stream()
                            .map(userResponse -> mapper.map(userResponse, CourierView.class))
                            .collect(Collectors.toList());
                    return new Page<>(courierViewList,
                            userResponsePage.getCurrentPage(),
                            userResponsePage.getTotalElements(),
                            userResponsePage.getNumberOfElements());
                })
                .switchIfEmpty(Mono.just(new Page<>(List.of(), 0, 0L, 0)));
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return parcelDeliveryApi.getOrder(orderId)
                .map(this::mapOrderResponseToOrderView);
    }

    @Override
    public Flux<CoordinateView> subscribeCourierCoordinates(UUID courierId) {
        return coordinateStream.getStream()
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
        return coordinateStream.publishToStream(courierCoordinateEvent);
    }

    private OrderView mapOrderResponseToOrderView(OrderResponse orderResponse) {
        return mapper.map(orderResponse, OrderView.class);
    }

    private OrderShortView mapOrderShortResponseToOrderShortView(OrderShortResponse orderShortResponse) {
        return mapper.map(orderShortResponse, OrderShortView.class);
    }
}
