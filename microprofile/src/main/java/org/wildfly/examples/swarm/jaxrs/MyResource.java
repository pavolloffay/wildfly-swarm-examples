package org.wildfly.examples.swarm.jaxrs;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.joda.time.DateTime;

/**
 * @author Bob McWhirter
 */
@Path("/")
public class MyResource {

    @Context
    private UriInfo uri;

    @GET
    @Produces("text/plain")
    public String get() {
        // Prove we can use an external dependency and weird JDK classes.
        return "Howdy at " + new DateTime() + ".  Have a JDK class: " + javax.security.auth.login.LoginException.class.getName();
    }

    @GET
    @Path("chaining")
    @Produces("text/plain")
    public String chaining() throws MalformedURLException, URISyntaxException {
        String inUrl = uri.getAbsolutePath().toString();
        inUrl = inUrl.replace("/chaining", "");
        System.out.println(inUrl);

        ClientBase client = RestClientBuilder.newBuilder()
            .baseUrl(new URL(inUrl))
            .build(ClientBase.class);

        Response response = client.get();
        String body = response.readEntity(String.class);
        response.close();

        // Prove we can use an external dependency and weird JDK classes.
        return "Chaining " + body;
    }

    @GET
    @Path("chainingException")
    @Produces("text/plain")
    public String chainingException() throws MalformedURLException, URISyntaxException {
        String inUrl = uri.getAbsolutePath().toString();
        inUrl = inUrl.replace("/chainingException", "") + "/exception";
        System.out.println(inUrl);

        ClientBase client = RestClientBuilder.newBuilder()
            .baseUrl(new URL(inUrl))
            .build(ClientBase.class);

        client.get();

        return "ChainingException ";
    }

    @GET
    @Path("exception")
    @Produces("text/plain")
    public String exception() {
        throw new RuntimeException();
    }

    @Path("/")
    public interface ClientBase {
        @GET
        @Path("/")
        Response get();
    }
}
