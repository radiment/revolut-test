package com.revolut.test.rest;

import com.revolut.test.dto.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.Arrays.asList;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @GET
    public List<User> helloWorld() {
        return asList(User.builder().name("bla").build());
    }
}
