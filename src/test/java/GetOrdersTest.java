import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Courier;
import model.Order;
import model.RespDataOrder;
import model.RespError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

public class GetOrdersTest {
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Integer courierId;
    Integer orderId;
    Boolean isDelCourier = false;

    @BeforeEach
    public void initEach() {
        Courier courier = new Courier("mary", "7890", "Мария");
        ApiSteps.createCourier(courier);
        courierId = ApiSteps.getIdCourier(courier);
        Order order = new Order("Марианна",
                "Ларионова",
                "Тверская",
                "31",
                "89123456789",
                "2",
                LocalDate.now().plusDays(1).format(DATE_FORMATTER),
                "позвонить",
                Set.of("BLACK"));
        Integer track = ApiSteps.createOrder(order);
        orderId = ApiSteps.getOrder(track).getId();
    }

    @Test
    @DisplayName("Список заказов")
    @Description("Проверка получения списка заказов без параметров:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Список заказов не пуст.")
    public void getOrders() {
        Map<String, String> queryParams = new HashMap<>();
        Set<RespDataOrder> respOrders = ApiSteps.getOrders(queryParams);
        assertFalse(respOrders.isEmpty());
    }

    @Test
    @DisplayName("Список заказов курьера, непринятого ни одного заказа")
    @Description("Проверка получения пустого списка заказов курьера:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Принятых заказов нет.")
    public void getOrdersEmptyWithCourier() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("courierId", String.valueOf(courierId));
        Set<RespDataOrder> respOrders = ApiSteps.getOrders(queryParams);
        assertTrue(respOrders.isEmpty());
    }


    @Test
    @DisplayName("Список заказов курьера, принятый заказ")
    @Description("Проверка получения списка заказов курьера:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Принятый заказ показан.")
    public void getOrdersWithCourier() {
        ApiSteps.acceptOrder(orderId, courierId);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("courierId", String.valueOf(courierId));
        Set<RespDataOrder> respOrders = ApiSteps.getOrders(queryParams);
        assertTrue(respOrders.stream()
                .map(RespDataOrder::getId)
                .anyMatch(i -> Objects.equals(i, orderId)));
    }

    @Test
    @DisplayName("Список заказов курьера с указанием станции")
    @Description("Проверка получения списка заказов курьера с указанием станции:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Принятый заказ показан.")
    public void getOrdersWithCourierStation() {
        ApiSteps.acceptOrder(orderId, courierId);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("courierId", String.valueOf(courierId));
        queryParams.put("nearestStation", "[\"31\"]");
        Set<RespDataOrder> respOrders = ApiSteps.getOrders(queryParams);
        assertTrue(respOrders.stream()
                .map(RespDataOrder::getId)
                .anyMatch(i -> Objects.equals(i, orderId)));
    }

    @Test
    @DisplayName("Список заказов с несуществующим id курьера")
    @Description("Проверка получения списка заказов с несуществующим id курьера:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки.")
    public void getOrdersWithNotExistCourier() {
        isDelCourier = ApiSteps.deleteCourier(courierId);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("courierId", String.valueOf(courierId));
        Response response = ApiRequests.sendGetRequestGetOrders(queryParams);
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        String expectedMsg = format("Курьер с идентификатором %s не найден", courierId);
        assertEquals(expectedMsg, respError.getMessage());
    }

    @AfterEach
    public void tearDown() {
        //API Отменить заказ не работает и невозможно почистить данные, удалить заказ
        if (!isDelCourier) ApiSteps.deleteCourier(courierId);
    }
}
