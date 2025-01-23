package springai.rag.poc.service;

import dev.langchain4j.data.embedding.Embedding;
import java.util.List;

public interface IStorageService {

    void storeChunks(List<Embedding> embeddings);
}
