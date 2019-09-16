package jersey.java8;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import jersey.java8.resource.RootResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {
  public AppConfig() {
    this.register(JacksonJaxbJsonProvider.class).register(RootResource.class);
  }
}
