package helidon.graal;

import com.google.cloud.Timestamp;
import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import lombok.extern.java.Log;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

@Log
public class UserService implements Service {
    private final ConcurrentMap<String, UserEntity> userStorage = new ConcurrentHashMap<>();

    @Override
    public void update(Routing.Rules rules) {
        rules
            .post("/", this::postCreate)
            .get("/{id}", this::getSelf);
    }

    public void postCreate(ServerRequest req, ServerResponse res) {
        req.content().as(JsonObject.class)
            .thenAccept(jo -> {
                String name = jo.getString("name");

                final String id = UUID.randomUUID().toString();
                final Instant now = Instant.now();

                final UserEntity entity = new UserEntity();
                entity.id = id;
                entity.name = name;
                entity.createdAt = Timestamp.of(Date.from(now));

                userStorage.put(id, entity);

                JsonObject json = Json.createObjectBuilder()
                    .add("id", id)
                    .add("name", name)
                    .add("createdAt", now.getEpochSecond())
                    .build();

                res.status(Http.Status.CREATED_201).send(json);
            }).exceptionally(ex -> {
            log.log(Level.WARNING, "Invalid Request", ex);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500);

            return null;
        });
    }

    public void getSelf(ServerRequest req, ServerResponse res) {
        String id = req.path().param("id");

        UserEntity entity = userStorage.get(id);
        if (entity == null) {
            res.status(Http.Status.NOT_FOUND_404);
            return;
        }

        JsonObject json = Json.createObjectBuilder()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("createdAt", entity.createdAt.getSeconds())
            .build();

        res.status(Http.Status.OK_200).send(json);
    }


}
