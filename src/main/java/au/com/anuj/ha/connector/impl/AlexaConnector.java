package au.com.anuj.ha.connector.impl;

import au.com.anuj.ha.connector.HAConnector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.UUID;

public class AlexaConnector extends HAConnector {

    public AlexaConnector() {
        super();
    }

    private static final String AWS_DEFAULT_REGION = "AWS_DEFAULT_REGION";

    @Override
    protected String getUrl() {
        return getBaseURL().concat("/alexa/smart_home");
    }

    protected HttpRequest.BodyPublisher getBody() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        AlexaDiscovery discovery = new AlexaDiscovery();
        discovery.getDirective().getPayload().getScope().setToken(getToken());
        String requestBody = gson.toJson(discovery);
        System.out.println(gson.toJson(discovery));
        return HttpRequest.BodyPublishers.ofString(requestBody);
    }


    @Override
    protected String getUserAgent() {
        final String region = (null != System.getenv(AWS_DEFAULT_REGION)) ? System.getenv(AWS_DEFAULT_REGION) : "aws-region";
        return LIBRARY.concat(" - ").concat(region).concat(" - Java 11");
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        AlexaConnector connector = new AlexaConnector();
        String response = connector.getDevices();
        System.out.println(response);
    }
}

@Data
class AlexaDiscovery {

    private Directive directive = new Directive();
}

@Data
class Directive {

    private Header header = new Header();
    private PayLoad payload = new PayLoad();
}

@Data
class Header {
    private String namespace = "Alexa.Discovery";

    private String name = "Discover";

    private String messageId = UUID.randomUUID().toString();

    private String payloadVersion = "3";
}

@Data
class PayLoad {
    private Scope scope = new Scope();
}

@Data
class Scope {
    private String type = "BearerToken";

    private String token;
}