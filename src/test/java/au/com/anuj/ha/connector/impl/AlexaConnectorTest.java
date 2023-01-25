package au.com.anuj.ha.connector.impl;

import au.com.anuj.ha.config.Configuration;
import au.com.anuj.ha.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("*")
class AlexaConnectorTest {

    private static final String CONFIG_FILE_NAME = "/config.json";
    private static final String SAMPLE_EVENT = "/sample-event.json";
    private AlexaConnector connector;
    private static Configuration configuration;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    static class AlexaConnectorForTests extends AlexaConnector {
        public AlexaConnectorForTests() throws JsonProcessingException {
            super();
        }

        public AlexaConnectorForTests(String event) throws JsonProcessingException {
            super(event);
        }

        @Override
        public HttpClient getHttpClient() {
            return super.getHttpClient();
        }

        @Override
        public Configuration getConfiguration() {
            return super.getConfiguration();
        }
    }


    @BeforeAll
    static void setup() throws JsonProcessingException {
        String configStr = new BufferedReader(new InputStreamReader(AlexaConnectorTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        configuration = Utils.returnPojoFromJson(config, Configuration.class);
    }

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void getUrl() throws JsonProcessingException {
        connector = new AlexaConnector();
        final String domain = configuration.getHomeAssistantURL();
        final String expectedValue = domain + "/api/alexa/smart_home";
        assertEquals(expectedValue, connector.getUrl());
    }

    @Test
    void getBody() throws JsonProcessingException {
        String sampleEvent = new BufferedReader(new InputStreamReader(AlexaConnectorTest.class.getResourceAsStream(SAMPLE_EVENT)))
                .lines().collect(Collectors.joining("\n"));
        connector = new AlexaConnector(sampleEvent);
        assertEquals(HttpRequest.BodyPublishers.ofString(sampleEvent).contentLength(), connector.getBody().contentLength());

        connector = new AlexaConnector();
        assertEquals(HttpRequest.BodyPublishers.noBody().contentLength(), connector.getBody().contentLength());
    }

    @Test
    void getUserAgentWithoutEnvVariable() throws Exception {
        connector = new AlexaConnector();
        assertEquals("Home Assistant Alexa Smart Home Skill - aws-region - Java 11", connector.getUserAgent());
    }

    @Test
    void getUserAgentWithEnvVariable() throws Exception {
        connector = new AlexaConnector();
        String userAgent = withEnvironmentVariable("AWS_DEFAULT_REGION", "us-west-2").execute(() -> connector.getUserAgent());
        assertEquals("Home Assistant Alexa Smart Home Skill - us-west-2 - Java 11", userAgent);
    }

    @Test
    void getDevices() throws IOException, InterruptedException {
        final String expectedResponse = "sample-response";

        AlexaConnectorForTests connectorForProtectedMethodTest = Mockito.mock(AlexaConnectorForTests.class);

        Configuration configuration = Mockito.mock(Configuration.class);
        when(configuration.getBearerToken()).thenReturn("sample-token");
        when(configuration.getDebug()).thenReturn(Boolean.TRUE);

        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(expectedResponse);

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        when(httpClient.send(any(), any())).thenReturn(httpResponse);


        when(connectorForProtectedMethodTest.getConfiguration()).thenReturn(configuration);
        when(connectorForProtectedMethodTest.getUserAgent()).thenReturn("sample-user-agent");
        when(connectorForProtectedMethodTest.getUrl()).thenReturn("http://localhost:8123");
        when(connectorForProtectedMethodTest.getBody()).thenReturn(HttpRequest.BodyPublishers.ofString("{ \"body\": \"sample-body\"}"));
        when(connectorForProtectedMethodTest.getHttpClient()).thenReturn(httpClient);
        when(connectorForProtectedMethodTest.getDevices()).thenCallRealMethod();

        String response = connectorForProtectedMethodTest.getDevices();
        assertEquals(expectedResponse, response);

        String sout = outputStreamCaptor.toString();
        assertTrue(sout.contains("Request URI:"));
        assertTrue(sout.contains("Request Headers:"));
        assertTrue(sout.contains("Request Body:"));
        assertTrue(sout.contains("Status code:"));
        assertTrue(sout.contains("Response body:"));
    }
}