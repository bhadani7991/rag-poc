package springai.rag.poc;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springai.rag.poc.service.impl.RetrievalService;

@SpringBootApplication
public class PocApplication implements CommandLineRunner {

	@Autowired
	private EmbeddingStore<TextSegment> embeddingStore;

	public static void main(String[] args) {
		SpringApplication.run(PocApplication.class, args);
	}


	@PreDestroy
	public void clearQdrantStore(){
		embeddingStore.removeAll();
	}

	@Autowired
	private RetrievalService retrievalService;

	@Override
	public void run(String... args) throws Exception {
		retrievalService.retrieveRelevantChunks("find employee detail whose last name is Facello",2)
				.forEach(System.out::println);
	}
}
