package springai.rag.poc.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springai.rag.poc.service.DocumentProcessor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static springai.rag.poc.constants.AppConstants.INGESTION_SUCCESS_RESPONSE;
import static springai.rag.poc.constants.AppConstants.METADATA_KEY;

@Service
@RequiredArgsConstructor
public class DocumentProcessorServiceImpl implements DocumentProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentProcessorServiceImpl.class);

    private final RestTemplate restTemplate;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    @Override
    public String processTopicDocuments(Map<String, String> topicUrlMap) {
        LOGGER.info("Started  Getting the topic URL Content...");
        List<Document> topicUrlDocumentsList = topicUrlMap.entrySet().stream()
                .map(topicUrlEntry->
                        Document.from(findTheUrlContent(topicUrlEntry.getValue()),
                                Metadata.metadata(METADATA_KEY,topicUrlEntry.getKey())))
                .toList();

        LOGGER.info("Completed fetching all the topic url content ");
        List<TextSegment> textSegmentList = new DocumentByWordSplitter(200,10)
                .splitAll(topicUrlDocumentsList);
        List<Embedding> embeddingList = embeddingModel.embedAll(textSegmentList).content();
        LOGGER.info("Embedding Completed for the Text Segments");
        embeddingStore.addAll(embeddingList,textSegmentList);
        LOGGER.info("Stored all the embeddings into Embedding Store(Vector Database)");
        return INGESTION_SUCCESS_RESPONSE;
    }

    private String findTheUrlContent(String url) {
        try {
            ResponseEntity<String> data =  restTemplate.getForEntity(url,String.class);
            org.jsoup.nodes.Document document = Jsoup.parse(Objects.requireNonNull(data.getBody()));
            return document.body().text().substring(0,3000);
        }catch (Exception e){
            throw new RuntimeException("Error occurred while fetching the url Content {}",e);
        }

    }
}
