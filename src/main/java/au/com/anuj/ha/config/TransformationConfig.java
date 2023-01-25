package au.com.anuj.ha.config;

import lombok.Data;

@Data
public class TransformationConfig {
    private String path;

    private String regex;

    private int groupExtractor;
}
