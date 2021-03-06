package jersey.java11;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;

public class Main {
  public static void main(final String[] args) throws InterruptedException {
    String port = System.getenv("PORT");
    if (port == null) port = "8080";

    ObjectifyService.init();

    final ObjectifyFactory factory = ObjectifyService.factory();
    factory.register(UserEntity.class);

    final AppConfig resourceConfig = new AppConfig();
    final HttpServer server =
        GrizzlyHttpServerFactory.createHttpServer(
            URI.create("http://0.0.0.0:" + port), resourceConfig);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  server.shutdown();
                }));

    Thread.currentThread().join();
  }
}
