package jersey.java11.resource;

import com.google.cloud.logging.LoggingHandler;
import lombok.extern.java.Log;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Log
@Path("/")
public class RootResource {
  static {
    LoggingHandler.addHandler(log, new LoggingHandler());
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getRoot() {
    log.info("Hello, world!");
    return "Hello, world!";
  }

  @Path("/users")
  public UserResource userResource() {
    return new UserResource();
  }
}
