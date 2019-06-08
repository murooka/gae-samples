package com.example.appengine.java8;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
class UserParams {
    @JsonProperty("name")
    String name;

    @Override
    public String toString() {
        return String.format("UserParams[name=%s]", this.name);
    }
}

@WebServlet("/users")
public class UsersServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UsersServlet.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserParams params = mapper.readValue(req.getReader(), UserParams.class);
        log.info(params.toString());

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Entity entity = new Entity("User");
        entity.setProperty("name", params.name);

        Key key = datastoreService.put(entity);

        UserView view = new UserView();
        view.id = KeyFactory.keyToString(key);
        view.name = params.name;

        res.setStatus(HttpURLConnection.HTTP_CREATED);
        res.setContentType("application/json");
        mapper.writeValue(res.getWriter(), view);
    }
}
