package springai.rag.poc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springai.rag.poc.assistant.Assistant;
import springai.rag.poc.service.DocumentProcessor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final Assistant assistant;
    private final DocumentProcessor documentProcessor;

    /**
     *  Api to process the documents and train the LLM
     *  with our documents so we can use them to retrieve
     *  relevant content from our documents.
     * @param topicUrlMap - map with key as topic and url as value
     *                     topic - Software Development ,
     *               url - link where we can find the software Development related content
     * @return Message - Successfully trained the LLM with the given topics
     */
    @PostMapping("/document/process")
    public ResponseEntity<String> preprocessDocumentsFromUrl(
            @RequestBody Map<String, String> topicUrlMap){
        return ResponseEntity.ok(documentProcessor.processTopicDocuments(topicUrlMap));
    }

    @GetMapping(value = "/chat")
    public ResponseEntity<String> aiAssistant(@RequestParam("message") String message){
        return ResponseEntity.ok(assistant.answer(message));
    }
}
