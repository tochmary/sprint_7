import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Courier;
import model.RespError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginCourierTest {

    Courier courier1, courier2;
    Integer courierId;

    @BeforeEach
    public void initEach() {
        courier1 = new Courier("mary", "7890", "Мария");
        ApiSteps.createCourier(courier1);
        courier2 = new Courier("dary", "3456", "Дарья");
        ApiSteps.createCourier(courier2);
        ApiSteps.deleteCourier(ApiSteps.getIdCourier(courier2));
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Проверка получения id курьера с корректными данными:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.\n")
    public void loginCourier() {
        courierId = ApiSteps.getIdCourier(courier1);
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера без логина")
    @Description("Проверка получения ошибки без логина:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.\n")
    public void loginCourierFailedWithoutLogin() {
        Courier courier = new Courier(null, courier1.getPassword());
        Response response = ApiRequests.sendPostRequestLoginCourier(courier);
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для входа", respError.getMessage());
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера без пароля")
    @Description("Проверка получения ошибки без пароля:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.\n")
    public void loginCourierFailedWithoutPassword() {
        Courier courier = new Courier(courier1.getLogin(), null);
        Response response = ApiRequests.sendPostRequestLoginCourier(courier);
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для входа", respError.getMessage());
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера c неправильным логином")
    @Description("Проверка получения ошибки c неправильным логином:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки.\n")
    public void loginCourierFailedWithWrongLogin() {
        Courier courier = new Courier(courier2.getLogin(), courier1.getPassword());
        ApiSteps.getNotExistIdCourier(courier);
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера c неправильным паролем")
    @Description("Проверка получения ошибки c неправильным паролем:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки.\n")
    public void loginCourierFailedWithWrongPassWord() {
        Courier courier = new Courier(courier1.getLogin(), courier2.getPassword());
        ApiSteps.getNotExistIdCourier(courier);
    }

    @AfterEach
    public void tearDown() {
        ApiSteps.deleteCourier(ApiSteps.getIdCourier(courier1));
    }
}
