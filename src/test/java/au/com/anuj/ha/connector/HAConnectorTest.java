package au.com.anuj.ha.connector;

import au.com.anuj.ha.config.Configuration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class HAConnectorTest {

    @Test
    void getHttpClient() {
        HAConnector haConnector = Mockito.spy(HAConnector.class);
        HttpClient client = haConnector.getHttpClient();
        assertNotNull(client);
    }

    @Test
    void getConfiguration() {
        HAConnector haConnector = Mockito.spy(HAConnector.class);
        Configuration c = haConnector.getConfiguration();
        assertNotNull(c);
    }
}