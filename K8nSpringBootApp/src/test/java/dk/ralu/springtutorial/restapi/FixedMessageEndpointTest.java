package dk.ralu.springtutorial.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // <== create an embedded web server that listens on a random port
public class FixedMessageEndpointTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedMessageEndpointTest.class);

    // @LocalServerPort is a shortcut for @Value("${local.server.port}")
    @LocalServerPort
    private int embeddedWebServerListenPort; // <== get the random port the embedded web server is bound to

    @Test
    public void index() throws Exception {
        URL url = new URL("http://localhost:" + embeddedWebServerListenPort + "/messages/fixed");
        LOGGER.info("Invoking " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertEquals("Hi World", readBody(connection));
    }

    // Java's built-in support for HTTP requests leads to really verbose code...
    private String readBody(HttpURLConnection connection) throws IOException {
        try (
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)
        ) {
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            return stringBuffer.toString().trim();
        }
    }
}
