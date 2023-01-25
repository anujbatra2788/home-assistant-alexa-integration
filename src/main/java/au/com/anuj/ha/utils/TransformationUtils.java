package au.com.anuj.ha.utils;

import au.com.anuj.ha.config.Configuration;
import au.com.anuj.ha.config.TransformationConfig;
import au.com.anuj.ha.connector.HAConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TransformationUtils {

    public static void transformResponse(JSONObject response, final Configuration configuration) {
        DocumentContext jsonDocContext;
        for(TransformationConfig config: configuration.getTransformationConfig()) {
            jsonDocContext = JsonPath.parse(response.toString());
            DocumentContext result = jsonDocContext.map(config.getPath(), (currentValue, pathConfiguration) -> {
                String transformedValue = transformValue(currentValue.toString(), config);
                System.out.println(currentValue + " ::: " + transformedValue);
                return transformedValue;
            });
            response = new JSONObject(result.jsonString());
        }
    }

    private static String transformValue(final String value, TransformationConfig config) {
        try {
            final Pattern p = Pattern.compile(config.getRegex());
            Matcher m = p.matcher(value);
            if (m.find()) {
                return m.group(config.getGroupExtractor()).trim();
            }
        } catch(Exception e) {

        }
        return value;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String fileName = "/config.json";
        String configStr = new BufferedReader(new InputStreamReader(TransformationUtils.class.getResourceAsStream(fileName)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject config = new JSONObject(configStr);
        Configuration c = Utils.returnPojoFromJson(config, Configuration.class);

        fileName = "/sample-response.json";
        configStr = new BufferedReader(new InputStreamReader(TransformationUtils.class.getResourceAsStream(fileName)))
                .lines().collect(Collectors.joining("\n"));
        JSONObject response = new JSONObject(configStr);
        transformResponse(response, c);
//        System.out.println(response.toString(2));
    }
}
