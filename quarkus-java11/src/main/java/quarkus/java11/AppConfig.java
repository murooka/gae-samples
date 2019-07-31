package quarkus.java11;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import quarkus.java11.resource.RootResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class AppConfig extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    final HashSet<Class<?>> classes = new HashSet<>();
    classes.add(ObjectifyFeature.class);
    classes.add(JacksonJaxbJsonProvider.class);
    classes.add(FallbackExceptionMapper.class);
    classes.add(RootResource.class);

    return classes;
  }
}
