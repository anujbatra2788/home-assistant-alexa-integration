package au.com.anuj.ha.config;

import au.com.anuj.ha.utils.Utils;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.binary.StringUtils;

import java.util.List;

@Data
@ToString
public class Configuration {

    private String homeAssistantURL;

    private String bearerToken;

    private Boolean debug;

    private Boolean sslVerify;

    private List<TransformationConfig> transformationConfig;

    public String getBearerToken() {
        return (null != Utils.getTokenFromEnv()) ? Utils.getTokenFromEnv() : this.bearerToken;
    }

    public String getHomeAssistantURL() {
        return (null != Utils.getHomeAssistantEndpoint()) ? Utils.getHomeAssistantEndpoint() : this.homeAssistantURL;
    }

}