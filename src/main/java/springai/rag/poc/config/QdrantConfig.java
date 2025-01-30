package springai.rag.poc.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Class to hold the qdrant related configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.qdrant")
public class QdrantConfig {
    private String host;
    private int port;
    private boolean useTLS;
    private String collectionName;
}
