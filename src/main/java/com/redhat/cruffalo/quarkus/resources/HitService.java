package com.redhat.cruffalo.quarkus.resources;

import com.redhat.cruffalo.quarkus.model.Hit;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.ZonedDateTime;

@Path("/hit")
public class HitService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String hit() {
        final Hit newHit = new Hit();
        newHit.message = "hello";
        newHit.timestamp = ZonedDateTime.now();
        newHit.persist();
        return newHit.message;
    }

}
