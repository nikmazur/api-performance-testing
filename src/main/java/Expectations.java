import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.RandomUtils;
import org.mockserver.client.MockServerClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class Expectations {

    private static MockServerClient mockServerClient = new MockServerClient("localhost", 8189);
    private static List<Long> data = Collections.synchronizedList(new ArrayList<>());

    public static void status() {
        mockServerClient
                .when(
                        request().withMethod("GET").withPath("/status"))
                .respond(
                        response().withStatusCode(200));
    }

    public static void addData() {
        mockServerClient
                .when(
                        request().withMethod("PUT").withPath("/data"))
                .respond(
                        httpRequest -> {
                            data.add(Long.valueOf((String) httpRequest.getBody().getValue()));
                            return response().withStatusCode(200).withBody(data.toString());
                        }
                );
    }

    public static void delData() {
        mockServerClient
                .when(
                        request().withMethod("DELETE").withPath("/data"))
                .respond(
                        httpRequest -> {
                            data.remove(0);
                            return response().withStatusCode(200).withBody(data.toString());
                        }
                );
    }

    public static void delayResp() {
        mockServerClient
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
        mockServerClient
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
