package quarkus.java11;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class FallbackExceptionMapper implements ExceptionMapper<Throwable> {
  @Override
  public Response toResponse(final Throwable exception) {
    log.error("got exception", exception);
    return Response.status(500).entity("internal server error").build();
  }
}
