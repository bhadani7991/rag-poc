package springai.rag.poc.processor;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import springai.rag.poc.modal.Employees;
import springai.rag.poc.repo.IEmployeeRepo;
import springai.rag.poc.service.IEmbeddingService;
import springai.rag.poc.service.IStorageService;
import java.util.ArrayList;
import java.util.List;

/**
 * Preprocess all the record of the employees
 * and do the chunks and embed all the record
 * and store into qdrant vector store for
 * similarity searching.
 */
@Component
public class DataProcessor {

    @Autowired
    private IEmployeeRepo employeeRepo;

    @Autowired
    private EmbeddingStore<TextSegment> qdrantEmbeddingStore;

    @Autowired
    private IEmbeddingService embeddingService;

    @Autowired
    private IStorageService storageService;

    @Autowired
    public void storeDataInStore(){

//        int pageSize = 1000;
//        int pageNumber = 0;
//
//        Page<Employees> page;
//        do {
//            // Fetch data in chunks
//            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
//            page = employeeRepo.findAll(pageRequest);
            List<String> employeeschunksData = chunkData(employeeRepo.findTop1000EmployeesOrderedByEmpNo());
            storageService.storeChunks(embeddingService.generateEmbeddings(employeeschunksData),employeeschunksData.stream().map(e->TextSegment.from(e)).toList());

//            pageNumber++;  // Move to the next batch
//        } while (page.hasNext());
    }

    // Chunking the data
    private List<String> chunkData(List<Employees> employees) {
        List<String> chunks = new ArrayList<>();

        for (Employees employee : employees) {
            String chunk = String.format("EmpNo: %d, Name: %s %s, Hire Date: %s",
                    employee.getEmpNo(), employee.getFirstName(), employee.getLastName(), employee.getHireDate());
            chunks.add(chunk);
        }
        return chunks;

    }
}
