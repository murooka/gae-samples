package jersey.java11.resource;

import com.google.cloud.Timestamp;
import com.google.cloud.logging.LoggingHandler;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import jersey.java11.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.java.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Log
public class UserResource {
  static {
    LoggingHandler.addHandler(log, new LoggingHandler());
  }

  @AllArgsConstructor
  @Value
  static class UserView {
    private final String id;
    private final String name;
    private final long createdAt;

    static UserView from(final UserEntity entity) {
      return new UserView(
          entity.id, entity.name, entity.createdAt.toDate().toInstant().getEpochSecond());
    }
  }

  @NoArgsConstructor
  @Data
  static class PostCreateBody {
    private String name;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserView postCreate(final PostCreateBody body) {
    log.info("User POST create: name = " + body.name);

    final String id = UUID.randomUUID().toString();
    final Instant now = Instant.now();

    final UserEntity entity = new UserEntity();
    entity.id = id;
    entity.name = body.getName();
    entity.createdAt = Timestamp.of(Date.from(now));
    ObjectifyService.ofy().save().entity(entity).now();

    return UserView.from(entity);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public UserView getSelf(@PathParam("id") final String id) {
    log.info("User GET self: id = " + id);

    final Key<UserEntity> key = Key.create(UserEntity.class, id);
    final UserEntity entity = ObjectifyService.ofy().load().key(key).now();

    return UserView.from(entity);
  }
}
