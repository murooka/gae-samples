package jersey.java11;

import com.google.cloud.ServiceOptions;
import com.google.cloud.logging.TraceLoggingEnhancer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class StackdriverLoggingFeature implements Feature {
  @Override
  public boolean configure(final FeatureContext context) {
    context.register(StackdriverLoggingContainerRequestFilter.class);

    return true;
  }

  static class StackdriverLoggingContainerRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext) {
      final String traceContext = requestContext.getHeaders().getFirst("x-cloud-trace-context");
      if (traceContext == null) return;

      final String[] parts = traceContext.split("/");
      if (parts.length == 0) return;

      final String traceId =
          String.format("projects/%s/traces/%s", ServiceOptions.getDefaultProjectId(), parts[0]);

      TraceLoggingEnhancer.setCurrentTraceId(traceId);
    }
  }
}
