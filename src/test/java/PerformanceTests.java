import com.github.noconnor.junitperf.*;
import com.github.noconnor.junitperf.data.TestContext;
import com.github.noconnor.junitperf.reporting.providers.ConsoleReportGenerator;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import server.RunServer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(JUnitPerfInterceptor.class)
public class PerformanceTests {

    private final static String S = File.separator;

    @JUnitPerfTestActiveConfig
    private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator(new File("").getAbsolutePath() + S + "reports" + S +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_TestReport.html"))
            .reportGenerator(new ConsoleReportGenerator())
            .build();

    @BeforeAll
    static void startServer() {
        RunServer.main(new String[]{""});
    }

    @Test
    @JUnitPerfTest(threads = 30, durationMs = 10_000, rampUpPeriodMs = 5_000, warmUpMs = 1_000, maxExecutionsPerSecond = 500)
    public void stressStatus() {
        Requests.getStatus().then().statusCode(200);
    }

    // Adds a random long number to the list on the API Server
    @Test
    @Order(1)
    @JUnitPerfTest(threads = 30, durationMs = 10_000, maxExecutionsPerSecond = 500)
    public void stressAddData() {
        Requests.addData(RandomUtils.nextLong(100_000_000_000L, 999_999_999_999L)).then().statusCode(200);
    }

    // Deletes a single number from the list which were added in previous test (hence the order number)
    @Test
    @Order(2)
    @JUnitPerfTest(threads = 30, durationMs = 9_000, maxExecutionsPerSecond = 500)
    public void stressDeleteData() {
        Requests.deleteData().then().statusCode(200);
    }

    // Accesses a method with an artificial delay (from 100 ms to 300 ms) on server side, which causes a significant
    // reduction in throughput. This test fails because we expect the max latency to not exceed 1 s (which it does).
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 20_000, maxExecutionsPerSecond = 500)
    @JUnitPerfTestRequirement(maxLatency = 1_000)
    public void stressDelayedResp() {
        Requests.getDelay().then().statusCode(200);
    }

    // Accesses a method which has a 90% to return 200, but in 10% returns 500.
    // Since this test has an allowed fail threshold set at 5%, this test will fail.
    @Test
    @JUnitPerfTest(threads = 30, durationMs = 10_000, maxExecutionsPerSecond = 500)
    @JUnitPerfTestRequirement(allowedErrorPercentage = 0.05F)
    public void stressRandomFail() {
        Requests.getFail().then().statusCode(200);
    }

    // Asynchronous test which uses several threads within the threaded test.
    // Simulates simultaneous addition and deletion of data.
    // Also has throughput pass requirement set at 100 tests per sec minimum.
    @Test
    @JUnitPerfTest(threads = 30, durationMs = 10_000, maxExecutionsPerSecond = 500)
    @JUnitPerfTestRequirement(executionsPerSec = 100)
    public void stressAddAndDelete(TestContextSupplier supplier) {
        TestContext context = supplier.startMeasurement();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        threadPool.submit( () -> {
            Requests.addData(RandomUtils.nextLong(100_000_000_000L, 999_999_999_999L)).then().statusCode(200);
            Requests.deleteData().then().statusCode(200);
            context.success();
        });
    }
}
