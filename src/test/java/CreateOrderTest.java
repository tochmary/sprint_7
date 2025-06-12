import helpers.ApiSteps;
import io.qameta.allure.Description;
import model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Stream;

public class CreateOrderTest extends ApiSteps {
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static Stream<Arguments> testColorData() {
        return Stream.of(
                Arguments.of(Set.of("BLACK")),
                Arguments.of(Set.of("GREY")),
                Arguments.of(Set.of("BLACK", "GREY")),
                Arguments.of(Set.of())
        );
    }

    @ParameterizedTest
    @MethodSource("testColorData")
    @DisplayName("Создание заказа")
    @Description("Проверка создания заказа с корректными данными:\n " +
            "1. Код и статус ответа 201 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказ создан.")
    public void createOrder(Set<String> color) {
        Order order = new Order("Марианна",
                "Ларионова",
                "Тверская",
                "31",
                "89123456789",
                "2",
                LocalDate.now().plusDays(1).format(DATE_FORMATTER),
                "позвонить",
                color);
        Integer track = createOrder(order);
        getOrder(track);
    }

    //API Отменить заказ не работает и невозможно почистить данные, удалить заказ
}
