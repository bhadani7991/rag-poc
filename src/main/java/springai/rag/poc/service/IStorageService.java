package springai.rag.poc.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;

public interface IStorageService {

    void storeChunks(List<Embedding> embeddings, List<TextSegment> textSegments);
}
