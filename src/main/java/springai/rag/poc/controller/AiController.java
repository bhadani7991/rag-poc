package springai.rag.poc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springai.rag.poc.assistant.Assistant;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final Assistant assistant;

    /**
     * Api to communicate with LLM and get
     *   the response from our trained data.
     * @param message - userMessage
     * @return String - returned response from LLM
     */
    @GetMapping(value = "/chat")
    public ResponseEntity<String> aiAssistant(@RequestParam("message") String message){
        return ResponseEntity.ok(assistant.answer(message));
    }
}
