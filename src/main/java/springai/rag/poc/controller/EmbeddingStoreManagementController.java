package springai.rag.poc.controller;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static springai.rag.poc.constants.AppConstants.EMBEDDING_STORE_CLEAR_RESPONSE;

@RestController
@RequestMapping("/qdrant")
@RequiredArgsConstructor
public class EmbeddingStoreManagementController {

    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * Api to remove all the embedding from Embedding Store.
     * @return String - returned Success Response
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> clearVectorStore(){
        try{
            embeddingStore.removeAll();
            return ResponseEntity.ok(EMBEDDING_STORE_CLEAR_RESPONSE);
        }catch (Exception e){
            throw new RuntimeException("Error occurred while clearing vector store");
        }
    }
}
