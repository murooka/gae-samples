package jersey.java11;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.google.cloud.ServiceOptions;
import com.google.cloud.logging.TraceLoggingEnhancer;
import com.google.cloud.logging.logback.LoggingAppender;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class StackdriverLoggingFeature implements Feature {
  @Override
  public boolean configure(final FeatureContext context) {
    final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    lc.reset();

    final LoggingAppender appender = new LoggingAppender();
    appender.setResourceType("gae_app");
    appender.start();

    final Logger logger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(appender);

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
