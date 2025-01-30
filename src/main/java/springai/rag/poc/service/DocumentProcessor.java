package springai.rag.poc.service;

import java.util.Map;

public interface DocumentProcessor {
    String processTopicDocuments(Map<String, String> topicUrlMap);
}
