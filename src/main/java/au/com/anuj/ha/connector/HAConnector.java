package au.com.anuj.ha.connector;

import au.com.anuj.ha.config.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HAConnector {

    protected static final String LIBRARY = "Home Assistant Alexa Smart Home Skill";
    private static Configuration configuration;

    public HAConnector() {
        Gson CONFIG = new Gson();
        Reader configFile = new InputStreamReader(HAConnector.class.getResourceAsStream("/config.json"));
        configuration = CONFIG.fromJson(configFile, TypeToken.get(Configuration.class));
        if(configuration.getDebug()) {
            System.out.println(configuration.toString());
        }
    }

    protected String getBaseURL() {
        return configuration.getHomeAssistantURL().concat("/api");
    }

    protected String getToken() {
        return configuration.getBearerToken();
    }

    protected abstract String getUrl();

    protected abstract String getUserAgent();

    protected HttpRequest.BodyPublisher getBody() {
        return HttpRequest.BodyPublishers.noBody();
    }

    public String getDevices() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        // Create the HttpRequest
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(getUrl()))
                .POST(getBody())
                .setHeader("Authorization", "Bearer " + configuration.getBearerToken())
                .setHeader("content-type", "application/json")
                .setHeader("User-Agent", getUserAgent())
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = "";
        if(configuration.getDebug()) {
            System.out.println("Request URI: " + httpRequest.uri());
            System.out.println("Request Body: " + httpRequest.bodyPublisher().toString());
            System.out.println("Status code: " + httpResponse.statusCode());
            body = httpResponse.body();
            System.out.println("Response body: " + body);
        }


        return body;
    }


}
