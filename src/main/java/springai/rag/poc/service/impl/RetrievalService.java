package springai.rag.poc.service.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class RetrievalService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ContentRetriever contentRetriever;

    public void retrieveRelevantChunks(String userQuery, int topK) {
        Embedding queryEmbedding = embeddingModel.embed(userQuery).content();

        List<Content> contents = contentRetriever.retrieve(Query.from(userQuery));
        contents.forEach(System.out::println);
    }
}
