package au.com.anuj.ha;

import au.com.anuj.ha.connector.HAConnector;
import au.com.anuj.ha.connector.impl.AlexaConnector;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Handler implements RequestHandler<Object, String> {
    @SneakyThrows
    @Override
    public String handleRequest(Object event, Context context) {
        LambdaLogger logger = context.getLogger();
        HAConnector connector = new AlexaConnector(""/*gson.toJson(event)*/);
        String response = null;
        try {
            response = connector.getDevices();
            logger.log("RESPONSE: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}