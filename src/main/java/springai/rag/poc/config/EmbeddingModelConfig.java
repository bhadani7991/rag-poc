package springai.rag.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class to hold the Embedding model configuration.
 */
@Component
@ConfigurationProperties(prefix = "spring.ai.model")
@Data
public class EmbeddingModelConfig {
    private String name;
}
