package springai.rag.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springai.rag.poc.assistant.Assistant;

@RestController
public class AiController {


    @Autowired
    private Assistant assistant;

    @GetMapping(value = "/chat")
    public ResponseEntity<String> aiAssistant(@RequestParam("message") String message){
        return ResponseEntity.ok(assistant.answer(message));
    }
}
