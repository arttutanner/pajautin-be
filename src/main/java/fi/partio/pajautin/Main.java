
package fi.partio.pajautin;



import fi.partio.pajautin.servlet.PajautinServletConfig;
import jakarta.ws.rs.core.UriBuilder;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;


import java.io.IOException;
import java.net.URI;




public class Main {

    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(9998).build();


    public static void main(String[] args) throws IOException {
        // Start grizzly server
        GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new PajautinServletConfig());


    }

}
