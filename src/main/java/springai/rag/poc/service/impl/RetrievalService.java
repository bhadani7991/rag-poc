package springai.rag.poc.service.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class RetrievalService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    public List<TextSegment> retrieveRelevantChunks(String userQuery, int topK) {
        Embedding queryEmbedding = embeddingModel.embed(userQuery).content();
        return embeddingStore.search(EmbeddingSearchRequest.builder()
                        .queryEmbedding(queryEmbedding)
                        .maxResults(2)
                        .minScore(0.2)
                        .build())
                .matches()
                .stream().map(EmbeddingMatch::embedded)
                .toList();
    }
}
