package jersey.java11;

import lombok.extern.java.Log;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Log
public class FallbackExceptionMapper implements ExceptionMapper<Throwable> {
  @Override
  public Response toResponse(Throwable exception) {
    log.severe(exception.getMessage());
    for (StackTraceElement e : exception.getStackTrace()) {
      log.severe(e.toString());
    }
    return Response.status(500).entity("internal server error").build();
  }
}
