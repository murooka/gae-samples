package helidon.java11;

import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.jersey.JerseySupport;

import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) throws Exception {
    WebServer webServer = WebServer
            .create(Routing.builder()
                    .register(JerseySupport.builder().register(Resource.class).build())
                    .build())
            .start()
            .toCompletableFuture()
            .get(10, TimeUnit.SECONDS);

    System.out.println("Server started at: http://localhost:" + webServer.port());
  }
}