package jersey.java11;

import lombok.extern.java.Log;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Log
public class FallbackExceptionMapper implements ExceptionMapper<Throwable> {
  @Override
  public Response toResponse(final Throwable exception) {
    log.severe("got exception: " + exception);
    return Response.status(500).entity("internal server error").build();
  }
}
