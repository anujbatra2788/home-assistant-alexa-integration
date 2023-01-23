package au.com.anuj.ha.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Configuration {

    private String homeAssistantURL;

    private String bearerToken;

    private Boolean debug;

    private Boolean sslVerify;
}
