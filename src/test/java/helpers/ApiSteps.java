package helpers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.*;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ApiSteps extends ApiRequests {

    @Step("Создание курьера")
    public void createCourier(Courier courier) {
        Response response = sendPostRequestCreateCourier(courier);
        response.then().statusCode(201);
        RespOk respOk = response.body().as(RespOk.class);
        assertTrue(respOk.isOk());
    }

    @Step("Удаление курьера c id {courierId}")
    public boolean deleteCourier(Integer courierId) {
        Response response = sendPostRequestDeleteCourier(String.valueOf(courierId));
        response.then().statusCode(200);
        RespOk respOk = response.body().as(RespOk.class);
        assertTrue(respOk.isOk());
        return respOk.isOk();
    }

    @Step("Логин курьера. Получение id курьера")
    public Integer getIdCourier(Courier courier) {
        Courier courierLogin = new Courier(courier.getLogin(), courier.getPassword());
        Response response = sendPostRequestLoginCourier(courierLogin);
        response.then().statusCode(200);
        RespId respId = response.body().as(RespId.class);
        assertNotNull(respId.getId());
        return respId.getId();
    }

    @Step("Логин курьера. Неуспешное получение id курьера")
    public Integer getNotExistIdCourier(Courier courier) {
        Response response = sendPostRequestLoginCourier(courier);
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Учетная запись не найдена", respError.getMessage());
        return null;
    }

    @Step("Создание заказа")
    public Integer createOrder(Order order) {
        Response response = sendPostRequestCreateOrder(order);
        response.then().statusCode(201);
        RespTrack respTrack = response.body().as(RespTrack.class);
        assertNotNull(respTrack.getTrack());
        return respTrack.getTrack();
    }

    @Step("Принятие заказа c id заказа {orderId} и id курьером {courierId}")
    public void acceptOrder(Integer orderId, Integer courierId) {
        Response response = sendPostRequestAcceptOrder(String.valueOf(orderId),
                String.valueOf(courierId));
        response.then().statusCode(200);
        RespOk respOk = response.body().as(RespOk.class);
        assertTrue(respOk.isOk());
    }

    @Step("Получение списка заказов")
    public Set<RespDataOrder> getOrders(Map<String, String> queryParams) {
        Response response = sendGetRequestGetOrders(queryParams);
        response.then().statusCode(200);
        RespOrders respOrders = response.body().as(RespOrders.class);
        assertNotNull(respOrders.getOrders());
        return respOrders.getOrders();
    }

    @Step("Получение заказа по его номеру {track}")
    public RespDataOrder getOrder(Integer track) {
        Response response = sendGetRequestGetOrder(track);
        response.then().statusCode(200);
        RespOrder respOrder = response.body().as(RespOrder.class);
        assertNotNull(respOrder.getOrder());
        return respOrder.getOrder();
    }
}
