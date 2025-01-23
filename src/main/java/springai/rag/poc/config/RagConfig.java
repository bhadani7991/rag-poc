package springai.rag.poc.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.neo4j.Neo4jEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springai.rag.poc.assistant.Assistant;
import javax.sql.DataSource;
import static java.time.Duration.ofSeconds;
import static springai.rag.poc.constants.AppConstants.COLLECTION_NAME;
import static springai.rag.poc.constants.AppConstants.MODAL_NAME_KEY;
import static springai.rag.poc.constants.AppConstants.NEO4J_HOST;
import static springai.rag.poc.constants.AppConstants.NEO4J_PASSWORD;
import static springai.rag.poc.constants.AppConstants.OPEN_API_KEY;
import static springai.rag.poc.constants.AppConstants.QDRANT_HOST;
import static springai.rag.poc.constants.AppConstants.TEMPERATURE;

/**
 *  Configuration class for configuring all the
 *  RAG Configuration.
 */
@Configuration
public class RagConfig {



    @Autowired
    private DataSource dataSource;
    @Bean
    public ChatLanguageModel chatLanguageModel(){
        return OpenAiChatModel.builder()
                .apiKey(System.getenv(OPEN_API_KEY))
                .modelName(System.getenv(MODAL_NAME_KEY))
                .temperature(TEMPERATURE)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }


    @Bean
    public EmbeddingModel embeddingModel(){
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv(OPEN_API_KEY))
                .modelName(System.getenv(MODAL_NAME_KEY))
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStoreForQdrant(){
        int grpcPort = 6333;
        return QdrantEmbeddingStore.builder()
                .host(QDRANT_HOST)
                .port(grpcPort)
                .collectionName(COLLECTION_NAME)
                .build();
    }

    @Primary
    @Bean
    public EmbeddingStore<TextSegment> embeddingStoreForNeo4J(){
        int grpcPort = 6333;
        return Neo4jEmbeddingStore.builder()
                .withBasicAuth(NEO4J_HOST, "neo4j", System.getenv(NEO4J_PASSWORD))
                .dimension(384)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> inMemmoryEmbeddingStore(){
        return new InMemoryEmbeddingStore<>();
    }


    @Bean
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel,
                                             EmbeddingStore<TextSegment> embeddingStoreForNeo4J){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStoreForNeo4J)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();
    }

    @Bean
    public Assistant aiServices(ChatLanguageModel chatLanguageModel,
                                 ContentRetriever contentRetriever){
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }


}
