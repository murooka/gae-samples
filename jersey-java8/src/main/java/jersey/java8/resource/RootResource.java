package jersey.java8.resource;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.java.Log;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Log
@Path("/")
public class RootResource {
  @AllArgsConstructor
  @Value
  static class OKView {
    private String message;
  }

  @GET
  @Path("/")
  @Produces(MediaType.TEXT_PLAIN)
  public String getRoot() {
    log.info("Hello, world!");
    return "Hello, world!";
  }

  @GET
  @Path("/ok")
  @Produces(MediaType.APPLICATION_JSON)
  public OKView getOK() {
    return new OKView("OK");
  }
}
