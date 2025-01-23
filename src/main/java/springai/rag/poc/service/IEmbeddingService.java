package springai.rag.poc.service;


import dev.langchain4j.data.embedding.Embedding;

import java.util.List;

public interface IEmbeddingService {
    List<Embedding> generateEmbeddings(List<String> chunks);
}
