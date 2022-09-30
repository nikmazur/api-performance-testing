import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;

public class RunServer {

    public static void main(String[] args) {
        // Server logging disabled to avoid flooding console
        ConfigurationProperties.logLevel("OFF");
        ClientAndServer.startClientAndServer(8189);

        Expectations.status();
        Expectations.addData();
        Expectations.delData();
        Expectations.delayResp();
        Expectations.randomFail();
    }
}