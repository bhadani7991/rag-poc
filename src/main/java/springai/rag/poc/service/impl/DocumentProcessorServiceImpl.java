package springai.rag.poc.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import springai.rag.poc.constants.AppConstants;
import springai.rag.poc.service.DocumentProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        return doDocumentEmbeddingAndIngestionProcess(topicUrlDocumentsList);
    }

    @Override
    public String processDocuments(List<MultipartFile> documents) {
        List<Document> documentList = documents.stream().map(file->{
            try {
                String contentType = file.getContentType();
                InputStream inputStream = file.getInputStream();

                return switch (contentType) {
                    case AppConstants.ContentType.APPLICATION_PDF-> Document.from(extractTextFromPDF(inputStream));
                    case AppConstants.ContentType.APPLICATION_TEXT -> Document.from(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
                    default -> throw new RuntimeException("Unsupported file type: " + contentType);
                };
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
        LOGGER.info("Successfully fetched all the file content");
        return doDocumentEmbeddingAndIngestionProcess(documentList);
    }

    private static String extractTextFromPDF(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private @NotNull String doDocumentEmbeddingAndIngestionProcess(List<Document> documentList) {
        try {
            List<TextSegment> textSegmentList = new DocumentByWordSplitter(1000, 50)
                    .splitAll(documentList);
            List<Embedding> embeddingList = embeddingModel.embedAll(textSegmentList).content();
            LOGGER.info("Embedding Completed for the Text Segments");
            embeddingStore.addAll(embeddingList, textSegmentList);
            LOGGER.info("Stored all the embeddings into Embedding Store(Vector Database)");
            return INGESTION_SUCCESS_RESPONSE;
        }catch (Exception e){
            throw new RuntimeException("Error occurred during embedding and ingestion process ",e);
        }
    }

    private String findTheUrlContent(String url) {
        try {
            ResponseEntity<String> data =  restTemplate.getForEntity(url,String.class);
            org.jsoup.nodes.Document document = Jsoup.parse(Objects.requireNonNull(data.getBody()));
            return document.body().text().substring(0,10000);
        }catch (Exception e){
            throw new RuntimeException("Error occurred while fetching the url Content {}",e);
        }

    }
    
    
}
