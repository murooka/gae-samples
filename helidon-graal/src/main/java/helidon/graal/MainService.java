package helidon.graal;

import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import lombok.extern.java.Log;

import javax.json.Json;
import javax.json.JsonObject;

@Log
public class MainService implements io.helidon.webserver.Service {
    @Override
    public void update(Routing.Rules rules) {
        rules
            .get("/ok", this::getOK);
    }

    public void getOK(ServerRequest req, ServerResponse res) {
        JsonObject json = Json.createObjectBuilder()
            .add("message", "OK")
            .build();

        res.status(Http.Status.OK_200).send(json);
    }
}
