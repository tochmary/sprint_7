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

public class DeleteCourierTest {

    Courier courier;
    Integer courierId;
    Boolean isDelCourier = false;

    @BeforeEach
    public void initEach() {
        courier = new Courier("mary", "7890", "Мария");
        ApiSteps.createCourier(courier);
        courierId = ApiSteps.getIdCourier(courier);
    }

    @Test
    @DisplayName("Удаление курьера с существующим id")
    @Description("Проверка удаления курьера с существующим id:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Курьер удален.")
    public void deleteCourier() {
        isDelCourier = ApiSteps.deleteCourier(courierId);
        courierId = ApiSteps.getNotExistIdCourier(courier);
    }


    @Test
    @DisplayName("Удаление курьера без id")
    @Description("Проверка неуспешного удалении курьера без id:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void deleteCourierFailedWithoutId() {
        Response response =  ApiRequests.sendPostRequestDeleteCourier("");
        response.then().statusCode(400);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Недостаточно данных для удаления курьера", respError.getMessage());
    }

    @Test
    @DisplayName("Удаление курьера с несуществующим id")
    @Description("Проверка неуспешного удалении курьера с несуществующим id:\n " +
            "1. Код и статус ответа 404 Not Found;\n" +
            "2. В ответе описание ошибки.")
    public void deleteCourierFailedWithNotExistId() {
        isDelCourier = ApiSteps.deleteCourier(courierId);

        Response response =  ApiRequests.sendPostRequestDeleteCourier(String.valueOf(courierId));
        response.then().statusCode(404);
        RespError respError = response.body().as(RespError.class);
        assertEquals("Курьера с таким id нет", respError.getMessage());
    }

    @AfterEach
    public void tearDown() {
        if (!isDelCourier) ApiSteps.deleteCourier(courierId);
    }
}
