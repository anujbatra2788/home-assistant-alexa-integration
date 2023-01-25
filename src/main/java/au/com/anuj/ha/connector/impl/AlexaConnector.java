package au.com.anuj.ha.connector.impl;

import au.com.anuj.ha.connector.HAConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.UUID;

public class AlexaConnector extends HAConnector {

    private String body = null;

    public AlexaConnector() throws JsonProcessingException {
        super();
    }

    public AlexaConnector(final String event) throws JsonProcessingException {
        super();
        this.body = event;
    }

    private static final String AWS_DEFAULT_REGION = "AWS_DEFAULT_REGION";

    @Override
    protected String getUrl() {
        return getBaseURL().concat("/alexa/smart_home");
    }

    protected HttpRequest.BodyPublisher getBody() {
        if(null == body) {
            return HttpRequest.BodyPublishers.noBody();
        } else {
            return HttpRequest.BodyPublishers.ofString(body);
        }
    }

    @Override
    protected String getUserAgent() {
        final String region = (null != System.getenv(AWS_DEFAULT_REGION)) ? System.getenv(AWS_DEFAULT_REGION) : "aws-region";
        return LIBRARY.concat(" - ").concat(region).concat(" - Java 11");
    }
}