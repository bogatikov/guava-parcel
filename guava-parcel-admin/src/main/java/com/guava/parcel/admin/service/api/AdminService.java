package com.guava.parcel.admin.service.api;

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
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.Status;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AdminService {

    Mono<SignInView> signIn(SignInForm signInForm);

    Mono<UserView> createCourier(CreateCourierForm createCourierForm);

    Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm);

    Mono<Page<OrderShortView>> getOrders(Status status, @NonNull Integer page, @NonNull Integer size);

    Mono<OrderView> setCourier(SetCourierForm setCourierForm);

    Mono<Page<CourierView>> getCouriers();

    Mono<OrderView> getOrder(UUID orderId);

    Flux<CoordinateView> subscribeCourierCoordinates(UUID courierId);

    Mono<Void> consumeCourierCoordinateEvent(CourierCoordinateEvent courierCoordinateEvent);
}
