package springai.rag.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai")
@Data
public class ChatLanguageModalConfig {
    private String modelName;
    private double temperature;
    private long timeout;
    private boolean logRequests;
    private boolean logResponse;
    private int chatMemory;
}
