package helidon.java11;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.jersey.JerseySupport;

import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) throws Exception {
    String envPort = System.getenv("PORT");
    int port = envPort != null ? Integer.parseInt(envPort) : 8000;
    
    ServerConfiguration configuration = ServerConfiguration.builder()
            .port(port)
            .build();

    WebServer webServer = WebServer
            .create(configuration, Routing.builder()
                    .register(JerseySupport.builder().register(Resource.class).build())
                    .build())
            .start()
            .toCompletableFuture()
            .get(10, TimeUnit.SECONDS);

    System.out.println("Server started at: http://localhost:" + webServer.port());
  }
}