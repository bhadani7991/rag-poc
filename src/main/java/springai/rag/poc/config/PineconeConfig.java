package springai.rag.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.pinecone")
public class PineconeConfig {
    private String indexName;
    private String cloud;
    private String region;
}
