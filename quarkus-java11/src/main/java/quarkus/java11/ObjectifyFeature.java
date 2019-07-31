package quarkus.java11;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.io.IOException;

public class ObjectifyFeature implements Feature {
  private static final String OFY_SESSION_KEY = "app.objectify_session";

  @Override
  public boolean configure(final FeatureContext context) {
    context.register(ObjectifyRequestFilter.class).register(ObjectifyResponseFilter.class);
    return true;
  }

  static class ObjectifyRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
      final Closeable session = ObjectifyService.begin();
      requestContext.setProperty(OFY_SESSION_KEY, session);
    }
  }

  static class ObjectifyResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(
        final ContainerRequestContext requestContext,
        final ContainerResponseContext responseContext)
        throws IOException {
      final Closeable session = (Closeable) requestContext.getProperty(OFY_SESSION_KEY);
      session.close();
    }
  }
}
