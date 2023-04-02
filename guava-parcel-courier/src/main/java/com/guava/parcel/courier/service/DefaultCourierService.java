package com.guava.parcel.courier.service;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.CoordinateForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.dto.view.CoordinateView;
import com.guava.parcel.courier.dto.view.CourierStatsView;
import com.guava.parcel.courier.dto.view.CourierView;
import com.guava.parcel.courier.dto.view.OrderShortView;
import com.guava.parcel.courier.dto.view.OrderView;
import com.guava.parcel.courier.dto.view.SignInView;
import com.guava.parcel.courier.error.EntityNotFound;
import com.guava.parcel.courier.event.CourierCoordinateEvent;
import com.guava.parcel.courier.ext.AuthApi;
import com.guava.parcel.courier.ext.ParcelDeliveryApi;
import com.guava.parcel.courier.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.courier.ext.request.SignInRequest;
import com.guava.parcel.courier.ext.response.UserResponse;
import com.guava.parcel.courier.model.Page;
import com.guava.parcel.courier.model.UserType;
import com.guava.parcel.courier.service.api.BroadcastService;
import com.guava.parcel.courier.service.api.CourierService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultCourierService implements CourierService {
    private final AuthApi authApi;
    private final ParcelDeliveryApi deliveryApi;
    private final ModelMapper mapper;
    private final BroadcastService broadcastService;

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return authApi.signIn(new SignInRequest(signInForm.email(), signInForm.password()))
                .map(signInResponse -> new SignInView(signInResponse.accessToken(), signInResponse.refreshToken()));
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders(Integer page, Integer size) {
        return resolveCourierId()
                .flatMap(courierId -> deliveryApi.getOrders(courierId, page, size))
                .map(responsePage -> new Page<>(
                                responsePage.getContent().stream()
                                        .map(orderShortResponse -> mapper.map(orderShortResponse, OrderShortView.class))
                                        .collect(Collectors.toList()),
                                responsePage.getCurrentPage(),
                                responsePage.getTotalElements(),
                                responsePage.getNumberOfElements()
                        )
                );
    }

    @Override
    public Mono<OrderView> changeStatus(ChangeOrderStatusForm changeStatusForm) {
        return resolveCourierId()
                .zipWith(deliveryApi.getOrder(changeStatusForm.orderId()))
                .filter(tuple -> tuple.getT1().equals(tuple.getT2().getCourierId()))
                .switchIfEmpty(Mono.error(new EntityNotFound("This courier can't change the order status")))
                .flatMap(tuple -> deliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(changeStatusForm.orderId(), changeStatusForm.status())))
                .map(orderResponse -> mapper.map(orderResponse, OrderView.class));
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return resolveCourierId()
                .zipWith(deliveryApi.getOrder(orderId))
                .filter(tuple -> tuple.getT1().equals(tuple.getT2().getCourierId()))
                .switchIfEmpty(Mono.error(new EntityNotFound("This courier can't change the order status")))
                .map(Tuple2::getT2)
                .map(orderResponse -> mapper.map(orderResponse, OrderView.class));
    }

    @Override
    public Mono<CoordinateView> sendCourierCoordinates(CoordinateForm coordinateForm) {
        return resolveCourierId()
                .flatMap(courierId -> broadcastService
                        .broadcastCourierCoordinateEvent(
                                new CourierCoordinateEvent(
                                        courierId,
                                        coordinateForm.longitude(),
                                        coordinateForm.latitude())
                        )
                        .thenReturn(new CoordinateView(courierId, coordinateForm.longitude(), coordinateForm.latitude())));
    }

    @Override
    public Mono<Page<CourierView>> getCourierList(Integer page, Integer size) {
        return authApi.getUserList(UserType.COURIER, page, size)
                .flatMap(this::handleCouriersPage)
                .switchIfEmpty(Mono.just(new Page<>(List.of(), page, 0L, 0)));
    }

    private Mono<Page<CourierView>> handleCouriersPage(Page<UserResponse> page) {
        return retrieveCouriersStats(page.getContent())
                .map(courierViews -> new Page<>(courierViews, page.getCurrentPage(), page.getTotalElements(), page.getNumberOfElements()));
    }

    private Mono<List<CourierView>> retrieveCouriersStats(List<UserResponse> content) {
        return Flux.fromIterable(content)
                .flatMap(courier -> retrieveCourierStats(courier)
                        .map(courierStatsView -> new CourierView(courier.getLastName(), courier.getFirstName(), courierStatsView.getStats()))
                )
                .collectList();
    }

    private Mono<CourierStatsView> retrieveCourierStats(UserResponse courier) {
        return deliveryApi.getCourierStats(courier.getId())
                .map(courierStatsResponse -> mapper.map(courierStatsResponse, CourierStatsView.class));
    }

    private Mono<UUID> resolveCourierId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> UUID.fromString((String) authentication.getPrincipal()));
    }
}
