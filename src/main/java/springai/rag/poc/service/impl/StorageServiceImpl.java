package springai.rag.poc.service.impl;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springai.rag.poc.service.IStorageService;

import java.util.List;

/**
 *  StorageService to store
 *  the embedded data in vector store.
 */
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;


    public void storeChunks(List<Embedding> embeddings,List<TextSegment> segments) {
        embeddingStore.addAll(embeddings,segments);
    }

}
