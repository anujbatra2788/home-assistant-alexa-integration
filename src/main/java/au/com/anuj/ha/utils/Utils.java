package au.com.anuj.ha.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.JSONObject;

public class Utils {

    private static final String HA_LONG_LIVED_TOKEN_PROP = "HA_LONG_LIVED_TOKEN";
    private static final String HA_ENDPOINT_PROP = "HA_ENDPOINT";

    public static String getTokenFromEnv() {
        return System.getenv(HA_LONG_LIVED_TOKEN_PROP);
    }

    public static String getHomeAssistantEndpoint() {
        return System.getenv(HA_ENDPOINT_PROP);
    }

    public static <T> T returnPojoFromJson(final JSONObject jsonObject, final Class<T> pojoClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule());
        T data = (T) mapper.readValue(jsonObject.toString(), pojoClass);
        return data;
    }
}
