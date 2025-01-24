package springai.rag.poc.service.impl;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springai.rag.poc.service.IEmbeddingService;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 *  EmbeddingService to embed the data chunks
 *  using OpenAiEmbeddingModal.
 */
@Service
public class EmbeddingServiceImpl implements IEmbeddingService {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ExecutorService executorService;

    @Override
    public List<Embedding> generateEmbeddings(List<String> chunks) {
        List<TextSegment> textSegmentList = chunks.stream()
                .map(TextSegment::from).toList();
        return embeddingModel.embedAll(textSegmentList).content();


    }
}
