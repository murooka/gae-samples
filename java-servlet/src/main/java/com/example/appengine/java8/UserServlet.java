package com.example.appengine.java8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getPathInfo().substring(1);
        log.info(id);


        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.stringToKey(id);
        Entity entity;

        try {
            entity = datastoreService.get(key);
        } catch (EntityNotFoundException e) {
            res.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return;
        }


        UserView view = new UserView();
        view.id = id;
        view.name = (String)entity.getProperty("name");

        res.setStatus(HttpURLConnection.HTTP_OK);
        res.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getWriter(), view);
    }
}
