import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptOrderTest {
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    Courier courier;
    Integer courierId;
    Integer track;
    Integer orderId;
    Boolean isDelCourier = false;

    @BeforeEach
    public void initEach() {
        courier = new Courier("mary", "7890", "Мария");
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
        track = ApiSteps.createOrder(order);
        orderId = ApiSteps.getOrder(track).getId();
    }

    @Test
    @DisplayName("Принять заказ")
    @Description("Проверка принятие заказа с корректными данными:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказ принят.")
    public void acceptOrder() {
        ApiSteps.acceptOrder(orderId, courierId);
        checkStatusAccept(track);
    }

    @Test
    @DisplayName("Неуспешное принятие заказа без id курьера")
    @Description("Проверка неуспешного принятия заказа без переданного id курьера:\n " +
            "1. Код и статус ответа 400 Conflict;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Заказ не принят.")
    public void acceptOrderFailedWithoutCourierId() {
        Response response = ApiRequests.sendPostRequestAcceptOrder(String.valueOf(orderId), "");
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для поиска", respError.getMessage());
        checkStatusNoAccept(track);
    }

    @Test
    @DisplayName("Неуспешное принятие заказа без id заказа")
    @Description("Проверка неуспешного принятия заказа без переданного id заказа:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Заказ не принят.")
    public void acceptOrderFailedWithoutOrderId() {
        Response response = ApiRequests.sendPostRequestAcceptOrder("", String.valueOf(courierId));
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для поиска", respError.getMessage());
        checkStatusNoAccept(track);
    }

    @Test
    @DisplayName("Неуспешное принятие заказа c несуществующим id курьера")
    @Description("Проверка неуспешного принятия заказа c несуществующим id курьера:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Заказ не принят.")
    public void acceptOrderFailedWithNotExistCourierId() {
        isDelCourier = ApiSteps.deleteCourier(courierId);
        Response response = ApiRequests.sendPostRequestAcceptOrder(String.valueOf(orderId), String.valueOf(courierId));
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Курьера с таким id не существует", respError.getMessage());
        checkStatusNoAccept(track);
    }

    @Test
    @DisplayName("Неуспешное принятие заказа c несуществующим id заказа")
    @Description("Проверка неуспешного принятия заказа c несуществующим id заказа:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Заказ не принят.")
    public void acceptOrderFailedNotExistOrderId() {
        Response response = ApiRequests.sendPostRequestAcceptOrder("0", String.valueOf(courierId));
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Заказа с таким id не существует", respError.getMessage());
        checkStatusNoAccept(track);
    }

    @Step("Проверка статуса заказа - принят курьером")
    private void checkStatusAccept(Integer track) {
        RespDataOrder respOrder = ApiSteps.getOrder(track);
        assertEquals(1, respOrder.getStatus(), "Статус не принят!");
        assertEquals(courier.getFirstName(), respOrder.getCourierFirstName(), "Курьер не назначат!");
    }

    @Step("Проверка статуса заказа - не принят курьером")
    private void checkStatusNoAccept(Integer track) {
        RespDataOrder respOrder = ApiSteps.getOrder(track);
        assertEquals(0, respOrder.getStatus(), "Статус принят!");
        assertNull(respOrder.getCourierFirstName(), "Курьер назначат!");
    }

    @AfterEach
    public void tearDown() {
        //API Отменить заказ не работает и невозможно почистить данные, удалить заказ
        if (!isDelCourier) ApiSteps.deleteCourier(courierId);
    }
}
