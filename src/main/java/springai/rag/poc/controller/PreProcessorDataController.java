package springai.rag.poc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springai.rag.poc.service.DocumentProcessor;
import java.util.List;
import java.util.Map;


/**
 * Controller class to handle the preprocessing of the documents
 * for RAG process.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/process")
public class PreProcessorDataController {

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
    @PostMapping("/urls")
    public ResponseEntity<String> preprocessDocumentsFromUrl(
            @RequestBody Map<String, String> topicUrlMap){
        return ResponseEntity.ok(documentProcessor.processTopicDocuments(topicUrlMap));
    }

    /**
     * Api to process the documents and train the LLM
     * with our documents so we can use them to retrieve
     * relevant content from our documents.
     * @param documents - files uploaded by user
     * @return String - success message.
     */
    @PostMapping("/documents")
    public ResponseEntity<String> processDocuments(@RequestParam List<MultipartFile> documents){
        return ResponseEntity.ok(documentProcessor.processDocuments(documents));

    }
}
