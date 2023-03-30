package com.guava.parcel.admin.ext;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.guava.parcel.admin.BaseIT;
import com.guava.parcel.admin.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.admin.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest(httpPort = 9999)
class ParcelDeliveryApiIT extends BaseIT {

    @Autowired
    ParcelDeliveryApi parcelDeliveryApi;

    @Test
    void changeOrderStatus() {
        stubFor(post(urlPathEqualTo("/guava-delivery/order/changeStatus"))
                .withRequestBody(equalToJson("{\"orderId\": \"24f8b54f-0f4f-41a7-9f16-38f5419ee557\", \"status\":  \"FINISHED\"}"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "id": "24f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "userId": "14f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "courierId": "34f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "sourceAddress": "The 2d Avenue",
                                          "destinationAddress": "The 3d Avenue",
                                          "status": "FINISHED",
                                          "updatedAt": null,
                                          "createdAt": "2023-03-28T00:00:00.000Z"
                                        }
                                        """)
                )
        );

        StepVerifier.create(parcelDeliveryApi.changeOrderStatus(
                                new ChangeOrderStatusRequest(
                                        UUID.fromString("24f8b54f-0f4f-41a7-9f16-38f5419ee557"),
                                        Status.FINISHED
                                )
                        )
                )
                .assertNext(orderResponse -> {
                    assertEquals("24f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.id().toString());
                    assertEquals("14f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.userId().toString());
                    assertEquals("34f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.courierId().toString());
                    assertEquals("The 2d Avenue", orderResponse.sourceAddress());
                    assertEquals("The 3d Avenue", orderResponse.destinationAddress());
                    assertEquals(Status.FINISHED, orderResponse.status());
                    assertNull(orderResponse.updatedAt());
                    assertEquals(Instant.parse("2023-03-28T00:00:00.000Z"), orderResponse.createdAt());
                })
                .verifyComplete();
    }

    @Test
    void getOrders() {
        stubFor(get(urlPathEqualTo("/guava-delivery/order/list"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("20"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "content": [
                                            {
                                              "id": "24f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                              "userId": "14f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                              "status": "FINISHED",
                                              "updatedAt": null,
                                              "createdAt": "2023-03-28T00:00:00.000Z"
                                            }
                                          ],
                                          "currentPage": 0,
                                          "totalElements": 1,
                                          "numberOfElements": 1
                                        }
                                        """)
                )
        );

        StepVerifier.create(parcelDeliveryApi.getOrders(Status.FINISHED, 0, 20)
                )
                .assertNext(responsePage -> {
                    assertEquals(0, responsePage.getCurrentPage());
                    assertEquals(1, responsePage.getTotalElements());
                    assertEquals(1, responsePage.getNumberOfElements());
                    assertEquals(1, responsePage.getContent().size());
                    assertAll(responsePage.getContent().stream().map(order -> () -> {
                        assertEquals("24f8b54f-0f4f-41a7-9f16-38f5419ee557", order.id().toString());
                        assertEquals("14f8b54f-0f4f-41a7-9f16-38f5419ee557", order.userId().toString());
                        assertEquals(Status.FINISHED, order.status());
                        assertNull(order.updatedAt());
                        assertEquals(Instant.parse("2023-03-28T00:00:00.000Z"), order.createdAt());
                    }));
                })
                .verifyComplete();
    }

    @Test
    void getOrder() {
        stubFor(get(urlPathEqualTo("/guava-delivery/order/24f8b54f-0f4f-41a7-9f16-38f5419ee557"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "id": "24f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "userId": "14f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "courierId": "34f8b54f-0f4f-41a7-9f16-38f5419ee557",
                                          "sourceAddress": "The 2d Avenue",
                                          "destinationAddress": "The 3d Avenue",
                                          "status": "FINISHED",
                                          "updatedAt": null,
                                          "createdAt": "2023-03-28T00:00:00.000Z"
                                        }
                                        """)
                )
        );

        StepVerifier.create(parcelDeliveryApi.getOrder(UUID.fromString("24f8b54f-0f4f-41a7-9f16-38f5419ee557"))
                )
                .assertNext(orderResponse -> {
                    assertEquals("24f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.id().toString());
                    assertEquals("14f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.userId().toString());
                    assertEquals("34f8b54f-0f4f-41a7-9f16-38f5419ee557", orderResponse.courierId().toString());
                    assertEquals("The 2d Avenue", orderResponse.sourceAddress());
                    assertEquals("The 3d Avenue", orderResponse.destinationAddress());
                    assertEquals(Status.FINISHED, orderResponse.status());
                    assertNull(orderResponse.updatedAt());
                    assertEquals(Instant.parse("2023-03-28T00:00:00.000Z"), orderResponse.createdAt());
                })
                .verifyComplete();
    }

    @Test
    void setCourier() {
        // TODO: 31.03.2023
    }
}