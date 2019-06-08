package jersey.java8;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ObjectifyInitializer implements ServletContextListener {
  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    ObjectifyService.init();

    final ObjectifyFactory factory = ObjectifyService.factory();
    factory.register(UserEntity.class);
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {}
}
