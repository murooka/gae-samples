package helidon.graal;

import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.server.JsonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public final class Main {
    public static void main(final String[] args) throws IOException {
        setupLogging();

        String envPort = System.getenv("PORT");
        int port = envPort == null ? 8000 : Integer.parseInt(envPort);

        ServerConfiguration serverConfig = ServerConfiguration.builder()
            .port(port)
            .build();

        WebServer server = WebServer.create(serverConfig, createRouting());

        server.start()
            .thenAccept(ws -> {
                System.out.println("server is listening on http://localhost:" + ws.port());
                ws.whenShutdown().thenRun(() -> System.out.println("server will shutdown"));
            })
            .exceptionally(t -> {
                System.err.println("Startup failed: " + t.getMessage());
                t.printStackTrace(System.err);
                return null;
            });
    }

    private static Routing createRouting() {
        MetricsSupport metrics = MetricsSupport.create();
        MainService mainService = new MainService();
        HealthSupport health = HealthSupport.builder()
            .addLiveness(HealthChecks.healthChecks())
            .build();

        return Routing.builder()
            .register(JsonSupport.create())
            .register(health)
            .register(metrics)
            .register("/", mainService)
            .build();
    }

    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }

}
