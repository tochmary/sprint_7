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

public class CreateCourierTest {

    Courier courier;
    Integer courierId;

    @BeforeEach
    public void initEach() {
        courier = new Courier("mary", "7890", "Мария");
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Проверка создания курьера с корректными данными:\n " +
            "1. Код и статус ответа 201 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Курьер добавлен.")
    public void createCourier() {
        ApiSteps.createCourier(courier);

        courierId = ApiSteps.getIdCourier(courier);
    }

    @Test
    @DisplayName("Неуспешное создание существующего курьера")
    @Description("Проверка неуспешного создания курьера, логин которого уже существует в системе:\n " +
            "1. Код и статус ответа 409 Сonflict;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Курьер не добавлен.\n")
    public void createFailedSecondCourier() {
        ApiSteps.createCourier(courier);

        courierId = ApiSteps.getIdCourier(courier);

        Response response = ApiRequests.sendPostRequestCreateCourier(courier);
        response.then().statusCode(409);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Этот логин уже используется",
                respError.getMessage());
    }

    @Test
    @DisplayName("Неуспешное создание курьера без логина")
    @Description("Проверка неуспешного создания курьера без логина:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Курьер не добавлен.\n")
    public void createFailedCourierWithoutLogin() {
        courier.setLogin(null);
        Response response = ApiRequests.sendPostRequestCreateCourier(courier);
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для создания учетной записи",
                respError.getMessage());
    }

    @Test
    @DisplayName("Неуспешное создание курьера без пароля")
    @Description("Проверка неуспешного создания курьера без пароля:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки;\n" +
            "3. Курьер не добавлен.\n")
    public void createFailedCourierWithoutPassword() {
        courier.setPassword(null);
        Response response = ApiRequests.sendPostRequestCreateCourier(courier);
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для создания учетной записи",
                respError.getMessage());
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) ApiSteps.deleteCourier(courierId);
    }
}
