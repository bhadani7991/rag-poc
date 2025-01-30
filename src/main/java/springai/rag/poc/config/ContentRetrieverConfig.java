package springai.rag.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * class to hold the contentRetriever related
 * Configuration.
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.content-retriever")
public class ContentRetrieverConfig {
    private int maxResults;
    private double minScore;
}
