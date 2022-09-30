import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Requests {

    private static RequestSpecification baseRequest() {
        return given().baseUri("http://localhost:8189");
    }

    public static Response getStatus() {
        return baseRequest().basePath("/status").get();
    }

    public static Response addData(Long number) {
        return baseRequest().basePath("/data").body(String.valueOf(number)).put();
    }

    public static Response deleteData() {
        return baseRequest().basePath("/data").delete();
    }

    public static Response getDelay() {
        return baseRequest().basePath("/delay").get();
    }

    public static Response getFail() {
        return baseRequest().basePath("/fail").get();
    }
}
