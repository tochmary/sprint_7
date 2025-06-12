package helpers;

public class URL {
    private final static String HOST_TEST = "https://qa-scooter.praktikum-services.ru";

    public static String getHost() {
        if (System.getProperty("host") != null) {
            return System.getProperty("host");
        } else {
            return HOST_TEST;
        }
    }
}
