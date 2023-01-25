package au.com.anuj.ha.connector;

import au.com.anuj.ha.config.Configuration;
import au.com.anuj.ha.utils.TransformationUtils;
import au.com.anuj.ha.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public abstract class HAConnector {

    protected static final String LIBRARY = "Home Assistant Alexa Smart Home Skill";
    private Configuration configuration;
    protected HttpClient httpClient = HttpClient.newHttpClient();

    public HAConnector() throws JsonProcessingException {
        String configStr = new BufferedReader(new InputStreamReader(HAConnector.class.getResourceAsStream("/config.json")))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        configuration = Utils.returnPojoFromJson(config, Configuration.class);

        if(configuration.getDebug()) {
            System.out.println(configuration.toString());
        }
    }

    protected String getBaseURL() {
        return configuration.getHomeAssistantURL().concat("/api");
    }

//    protected String getToken() {
//        return configuration.getBearerToken();
//    }

    protected abstract String getUrl();

    protected abstract String getUserAgent();

    protected abstract HttpRequest.BodyPublisher getBody();

    public String getDevices() throws IOException, InterruptedException {


        // Create the HttpRequest
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(getUrl()))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(getBody())
                .setHeader("Authorization", "Bearer " + getConfiguration().getBearerToken())
                .setHeader("content-type", "application/json")
                .setHeader("User-Agent", getUserAgent())
                .build();

        HttpResponse<String> httpResponse = getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = httpResponse.body();
        if(getConfiguration().getDebug()) {
            System.out.println("Request URI: " + httpRequest.uri());
            System.out.println("Request Headers: " + httpRequest.headers().toString());
            System.out.println("Request Body: " + httpRequest.bodyPublisher().get().toString());
            System.out.println("Status code: " + httpResponse.statusCode());
            System.out.println("Response body: " + body);
        }
        return body;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

}
