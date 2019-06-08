package jersey.java11;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import jersey.java11.resource.RootResource;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {
  public AppConfig() {
    this.register(LoggingFeature.class)
        .register(ObjectifyFeature.class)
        .register(JacksonJaxbJsonProvider.class)
        .register(FallbackExceptionMapper.class)
        .register(RootResource.class);
  }
}