package jersey.java11.resource;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/")
public class RootResource {
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
