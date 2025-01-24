package springai.rag.poc;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocApplication {

	@Autowired
	private EmbeddingStore<TextSegment> embeddingStore;

	public static void main(String[] args) {
		SpringApplication.run(PocApplication.class, args);
	}


	@PreDestroy
	public void clearVectorStore(){
		embeddingStore.removeAll();
	}

}
