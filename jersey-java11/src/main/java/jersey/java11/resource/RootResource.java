package jersey.java11.resource;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource {
  @AllArgsConstructor
  @Value
  static class OKView {
    private String message;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getRoot() {
    return "Hello, world!";
  }

  @Path("/ok")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OKView getOK() {
    return new OKView("OK");
  }
}
