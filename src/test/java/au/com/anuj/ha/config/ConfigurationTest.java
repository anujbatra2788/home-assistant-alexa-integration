package au.com.anuj.ha.config;

import au.com.anuj.ha.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationTest {

    private static final String CONFIG_FILE_NAME = "/sample-config.json";

    @Test
    void shouldGetBearerTokenFromConfigFileWhenNoEnvVar() throws JsonProcessingException {
        String configStr = new BufferedReader(new InputStreamReader(ConfigurationTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        Configuration configuration = Utils.returnPojoFromJson(config, Configuration.class);
        assertEquals("TOKEN", configuration.getBearerToken());
    }

    @Test
    void shouldGetBearerTokenFromEnvVar() throws Exception {
        final String token = "some_short_token";
        String newToken = withEnvironmentVariable("HA_LONG_LIVED_TOKEN", token).execute(() -> {
            String configStr = new BufferedReader(new InputStreamReader(ConfigurationTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject config = new JSONObject(configStr);
            Configuration configuration = Utils.returnPojoFromJson(config, Configuration.class);
            return configuration.getBearerToken();
        });
        assertEquals(token, newToken);
    }

    @Test
    void shouldGetHomeAssistantURLFromConfigFileWhenNoEnvVar() throws JsonProcessingException {
        String configStr = new BufferedReader(new InputStreamReader(ConfigurationTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        Configuration configuration = Utils.returnPojoFromJson(config, Configuration.class);
        assertEquals("http://ha.local:8123", configuration.getHomeAssistantURL());
    }

    @Test
    void shouldGetHomeAssistantURLFromEnvVar() throws Exception {
        final String url = "http://homeassistant.local:8123";
        String newUrl = withEnvironmentVariable("HA_ENDPOINT", url).execute(() -> {
            String configStr = new BufferedReader(new InputStreamReader(ConfigurationTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject config = new JSONObject(configStr);
            Configuration configuration = Utils.returnPojoFromJson(config, Configuration.class);
            return configuration.getHomeAssistantURL();
        });
        assertEquals(url, newUrl);
    }

    @Test
    void getTransformationConfig() throws JsonProcessingException {

        String configStr = new BufferedReader(new InputStreamReader(ConfigurationTest.class.getResourceAsStream(CONFIG_FILE_NAME)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        Configuration configuration = Utils.returnPojoFromJson(config, Configuration.class);

        assertEquals(2, configuration.getTransformationConfig().size());

        TransformationConfig transformationConfig = configuration.getTransformationConfig().get(0);
        assertEquals("$.event.payload.endpoints[*]", transformationConfig.getPath());
        assertEquals("[^-]+$", transformationConfig.getRegex());
        assertEquals(2, transformationConfig.getGroupExtractor());

        transformationConfig = configuration.getTransformationConfig().get(1);
        assertEquals("$.event.payload.endpoints[*].friendlyName", transformationConfig.getPath());
        assertEquals("[^-]+$", transformationConfig.getRegex());
        assertEquals(0, transformationConfig.getGroupExtractor());
    }
}