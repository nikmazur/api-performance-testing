package server;

import org.apache.commons.lang3.RandomUtils;
import org.mockserver.client.MockServerClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class Expectations {

    private static final MockServerClient MOCK_SERVER_CLIENT = new MockServerClient("localhost", 8189);
    private static final List<Long> DATA = Collections.synchronizedList(new ArrayList<>());

    private Expectations() {
    }

    public static void status() {
        MOCK_SERVER_CLIENT
                .when(
                        request().withMethod("GET").withPath("/status"))
                .respond(
                        response().withStatusCode(200));
    }

    public static void addData() {
        MOCK_SERVER_CLIENT
                .when(
                        request().withMethod("PUT").withPath("/data"))
                .respond(
                        httpRequest -> {
                            DATA.add(Long.valueOf((String) httpRequest.getBody().getValue()));
                            return response().withStatusCode(200).withBody(DATA.toString());
                        }
                );
    }

    public static void delData() {
        MOCK_SERVER_CLIENT
                .when(
                        request().withMethod("DELETE").withPath("/data"))
                .respond(
                        httpRequest -> {
                            DATA.remove(0);
                            return response().withStatusCode(200).withBody(DATA.toString());
                        }
                );
    }

    public static void delayResp() {
        MOCK_SERVER_CLIENT
                .when(
                        request().withMethod("GET").withPath("/delay"))
                .respond(
                        request -> {
                            Thread.sleep(RandomUtils.nextInt(100, 300));
                            return response().withStatusCode(200);
                        });
    }

    // 10% chance of returning 500 (Server error)
    public static void randomFail() {
        MOCK_SERVER_CLIENT
                .when(
                        request().withMethod("GET").withPath("/fail"))
                .respond(
                        request -> {
                            if(RandomUtils.nextInt(0, 100) < 10)
                                return response().withStatusCode(500);
                            else
                                return response().withStatusCode(200);
                        });
    }
}
