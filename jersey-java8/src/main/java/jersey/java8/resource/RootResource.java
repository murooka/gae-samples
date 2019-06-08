package jersey.java8.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource {
  @GET
  @Path("/")
  @Produces(MediaType.TEXT_PLAIN)
  public String getRoot() {
    return "Hello, world!";
  }

  @Path("/users")
  public UserResource userResource() {
    return new UserResource();
  }
}
