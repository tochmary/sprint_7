package helpers;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import model.Order;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class ApiRequests {

    private final static RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(URL.getHost())
            .addHeader("Content-type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    @Step("Отправить GET запрос /api/v1/ping")
    public static Response sendGetPing() {
        return given()
                .spec(SPEC)
                .get("/api/v1/ping")
                .thenReturn();
    }

    @Step("Отправить POST запрос /api/v1/courier")
    public static Response sendPostRequestCreateCourier(Courier courier) {
        return given()
                .spec(SPEC)
                .body(courier)
                .post("/api/v1/courier")
                .thenReturn();
    }

    @Step("Отправить POST запрос /api/v1/courier/login")
    public static Response sendPostRequestLoginCourier(Courier courier) {
        return given()
                .spec(SPEC)
                .body(courier)
                .post("/api/v1/courier/login")
                .thenReturn();
    }

    @Step("Отправить DELETE запрос /api/v1/courier/{id}")
    public static Response sendPostRequestDeleteCourier(String courierId) {
        return given()
                .spec(SPEC)
                .pathParam("id", courierId)
                .delete("/api/v1/courier/{id}")
                .thenReturn();
    }

    @Step("Отправить POST запрос /api/v1/orders")
    public static Response sendPostRequestCreateOrder(Order order) {
        return given()
                .spec(SPEC)
                .body(order)
                .post("/api/v1/orders")
                .thenReturn();
    }

    @Step("Отправить PUT запрос /api/v1/orders/accept/:id")
    public static Response sendPostRequestAcceptOrder(String orderId, String courierId) {
        return given()
                .spec(SPEC)
                .pathParams("id", orderId)
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/{id}")
                .thenReturn();
    }

    @Step("Отправить GET запрос /api/v1/orders")
    public static Response sendGetRequestGetOrders(Map<String, String> queryParams) {
        return given()
                .spec(SPEC)
                .queryParams(queryParams)
                .get("/api/v1/orders")
                .thenReturn();
    }

    @Step("Отправить GET запрос /api/v1/orders/track")
    public static Response sendGetRequestGetOrder(Integer track) {
        return given()
                .spec(SPEC)
                .queryParam("t", track)
                .get("/api/v1/orders/track")
                .thenReturn();
    }
}
