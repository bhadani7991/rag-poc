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
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springai.rag.poc.assistant.Assistant;

import static java.time.Duration.ofSeconds;
import static springai.rag.poc.constants.AppConstants.OPEN_API_KEY;
import static springai.rag.poc.constants.AppConstants.QDRANT_API_KEY;

/**
 *  Configuration class for configuring all the
 *  RAG Configuration.
 */
@Configuration
@RequiredArgsConstructor
public class RagConfig {

    private final ChatLanguageModalConfig chatLanguageModalConfig;
    private final EmbeddingModelConfig embeddingModelConfig;
    private final QdrantConfig qdrantConfig;
    private final ContentRetrieverConfig contentRetrieverConfig;

    /**
     * configures an instance of `ChatLanguageModal` for
     * communicating with LLM.
     * @return ChatLanguageModel {@link OpenAiChatModel}
     */
    @Bean
    public ChatLanguageModel chatLanguageModel(){
        return OpenAiChatModel.builder()
                .apiKey(System.getenv(OPEN_API_KEY))
                .modelName(chatLanguageModalConfig.getModelName())
                .temperature(chatLanguageModalConfig.getTemperature())
                .timeout(ofSeconds(chatLanguageModalConfig.getTimeout()))
                .logRequests(chatLanguageModalConfig.isLogRequests())
                .logResponses(chatLanguageModalConfig.isLogResponse())
                .build();
    }

    /**
     * configures an instance of `EmbeddingModel` for
     * converting text into Embedding(Numerical Representation of text).
     * @return EmbeddingModel {@link OpenAiEmbeddingModel}
     */
    @Bean
    public EmbeddingModel embeddingModel(){
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv(OPEN_API_KEY))
                .modelName(embeddingModelConfig.getName())
                .build();
    }

    /**
     * configures an instance of `QdrantClient` for
     * connecting with Qdrant DB.
     * @return QdrantClient {@link QdrantClient}
     */
    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(
                                qdrantConfig.getHost(),
                                qdrantConfig.getPort(),
                                qdrantConfig.isUseTLS())
                        .withApiKey(System.getenv(QDRANT_API_KEY))
                        .build()
        );
    }

    /**
     * configures an instance of `EmbeddingStore` for
     *  storing the embeddings.
     * @param qdrantClient - to connect with Qdrant DB
     * @return EmbeddingStore {@link QdrantEmbeddingStore}
     */
    @Primary
    @Bean
    public EmbeddingStore<TextSegment> embeddingStoreForQdrant(QdrantClient qdrantClient){
        return QdrantEmbeddingStore.builder()
                .client(qdrantClient)
                .collectionName(qdrantConfig.getCollectionName())
                .build();
    }

    /**
     * configures an instance of `EmbeddingStoreIngestor` for
     *  ingesting document into embeddingstore.
     * @param embeddingModel - to embed the given text
     * @param embeddingStore - to store the embeddings
     * @return EmbeddingStoreIngestor -
     */
   @Bean
   public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel
                                                ,EmbeddingStore<TextSegment> embeddingStore){
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
   }

   /**
    * configures an instance of `EmbeddingStore` for
    *  storing the embeddings.
    */
   @Bean
   public EmbeddingStore<TextSegment> inMemmoryEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    /**
     * Configures and instance of `ContentRetriever` for
     * finding the relevant content from the embedding store.
     * @param embeddingModel - to convert text into embedding.
     * @param embeddingStore - to store the embedding.
     * @return ContentRetriever - Datasource from which the content
     *                  should be retrieved.
     */
    @Bean
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel,
                                             EmbeddingStore<TextSegment> embeddingStore){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(contentRetrieverConfig.getMaxResults())
                .minScore(contentRetrieverConfig.getMinScore())
                .build();
    }

    /**
     *
     * @param chatLanguageModel - LLM model which takes input process input
     *                          and capable of producing result based on
     *                          similarity search and memory.
     * @param contentRetriever - retrieve the content from the specified content
     *                         retriever.
     * @return Assistant {@link Assistant}
     */
    @Bean
    public Assistant aiServices(ChatLanguageModel chatLanguageModel,
                                 ContentRetriever contentRetriever){
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory
                        .withMaxMessages(chatLanguageModalConfig.getChatMemory()))
                .build();
    }


}
