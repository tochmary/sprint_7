import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Order;
import model.RespDataOrder;
import model.RespError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetOrderTest extends ApiSteps {
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    @DisplayName("Получение заказа по его номеру")
    @Description("Получение заказа по существующему номеру:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Данные заказа совпадают.")
    public void getOrder() {
        Order order = new Order("Марианна",
                "Ларионова",
                "Тверская",
                "31",
                "89123456789",
                "2",
                LocalDate.now().plusDays(1).format(DATE_FORMATTER),
                "позвонить",
                Set.of("BLACK"));
        Integer track = createOrder(order);
        RespDataOrder respOrder = getOrder(track);
        String expectedDate = OffsetDateTime.parse(respOrder.getDeliveryDate()).format(DATE_FORMATTER);
        assertAll("Проверка полей Order",
                () -> assertEquals(order.getFirstName(), respOrder.getFirstName()),
                () -> assertEquals(order.getFirstName(), respOrder.getFirstName()),
                () -> assertEquals(order.getLastName(), respOrder.getLastName()),
                () -> assertEquals(order.getAddress(), respOrder.getAddress()),
                () -> assertEquals(order.getMetroStation(), respOrder.getMetroStation()),
                () -> assertEquals(order.getPhone(), respOrder.getPhone()),
                () -> assertEquals(order.getRentTime(), String.valueOf(respOrder.getRentTime())),
                () -> assertEquals(order.getDeliveryDate(), expectedDate),
                () -> assertEquals(order.getComment(), respOrder.getComment()),
                () -> assertEquals(order.getColor(), respOrder.getColor()));
    }

    @Test
    @DisplayName("Получение заказа по его номеру без указания номера")
    @Description("Получение заказа по его номеру без указания номера:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void getOrderFailedWithoutTrack() {
        Response response = sendGetRequestGetOrder(null);
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для поиска", respError.getMessage());
    }

    @Test
    @DisplayName("Получение заказа по его номеру c несущ. номером")
    @Description("Получение заказа по его номеру без указания номера:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки.")
    public void getOrderFailedWithNotExistTrack() {
        Response response = sendGetRequestGetOrder(0);
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Заказ не найден", respError.getMessage());
    }
}