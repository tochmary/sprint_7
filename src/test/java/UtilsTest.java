
import helpers.ApiRequests;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    @Test
    @DisplayName("Ping server API: /api/v1/ping") // имя теста
    @Description("Проверка сервера")
    void getPingServer() {
        Response response = ApiRequests.sendGetPing();
        response.then().statusCode(200);
        assertEquals("pong;", response.getBody().asString(),
                "Ответ должен быть: pong;");
    }
}
