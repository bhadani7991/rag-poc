package springai.rag.poc.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface DocumentProcessor {
    String processTopicDocuments(Map<String, String> topicUrlMap);

    String processDocuments(List<MultipartFile> documents);
}
