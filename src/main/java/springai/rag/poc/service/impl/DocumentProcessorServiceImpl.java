package springai.rag.poc.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import springai.rag.poc.constants.AppConstants;
import springai.rag.poc.exception.RowLimitExceededException;
import springai.rag.poc.service.DocumentProcessor;
import springai.rag.poc.util.SheetHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static springai.rag.poc.constants.AppConstants.INGESTION_SUCCESS_RESPONSE;
import static springai.rag.poc.constants.AppConstants.METADATA_KEY;


@Service
@RequiredArgsConstructor
public class DocumentProcessorServiceImpl implements DocumentProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentProcessorServiceImpl.class);

    private final RestTemplate restTemplate;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ExecutorService executorService;

    @Override
    public String processTopicDocuments(Map<String, String> topicUrlMap) {
        LOGGER.info("Started  Getting the topic URL Content...");
        List<Document> topicUrlDocumentsList = topicUrlMap.entrySet().stream()
                .map(topicUrlEntry->
                        Document.from(findTheUrlContent(topicUrlEntry.getValue()),
                                Metadata.metadata(METADATA_KEY,topicUrlEntry.getKey())))
                .toList();

        LOGGER.info("Completed fetching all the topic url content ");
        return doDocumentEmbeddingAndIngestionProcess(topicUrlDocumentsList);
    }

    @Override
    public String processDocuments(List<MultipartFile> documents) {
        List<Document> documentList = documents.stream().map(file->{
            try {
                String contentType = file.getContentType();
                InputStream inputStream = file.getInputStream();

                return switch (contentType) {
                    case AppConstants.ContentType.APPLICATION_PDF-> Document.from(extractTextFromPDF(inputStream));
                    case AppConstants.ContentType.APPLICATION_TEXT -> Document.from(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
                    case AppConstants.ContentType.APPLICATION_EXCEL, AppConstants.ContentType.APPLICATION_XLSX -> Document.from(extractTextFromExcel(inputStream));
                    default -> throw new RuntimeException("Unsupported file type: " + contentType);
                };
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
        LOGGER.info("Successfully fetched all the file content");
        return doDocumentEmbeddingAndIngestionProcess(documentList);
    }


    private String extractTextFromExcelTesting(InputStream inputStream) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;
            List<Map<String, String>> data = new ArrayList<>();

            Row headerRow = sheet.getRow(0); // Get column headers
            if (headerRow == null) {
                throw new IOException("Empty Excel file");
            }

            for (Row row : sheet) {
                if (rowCount == 0) { rowCount++; continue; } // Skip header row
                if (rowCount > 10) break; // Limit to 10 rows

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell headerCell = headerRow.getCell(i);
                    Cell cell = row.getCell(i);
                    String header = headerCell != null ? headerCell.toString() : "Column" + i;
                    String value = cell != null ? cell.toString() : "";
                    rowData.put(header, value);
                }
                data.add(rowData);
                rowCount++;
            }

            workbook.close();
            return data.toString();
        }catch (IOException exception){
            throw new RuntimeException("Error occurred while processing Excel Document");
        }
    }

    /**
     * Using SAX(Simple Api for xml parsing)  to efficiently handle the Excel file f
     * or large excel file also utilised ExecutorService for Parallel processing.
     * @param inputStream - inputStream of Excel file
     * @return - String - content of all the excel file as String to create a document.
     */
    public  String extractTextFromExcel(InputStream inputStream) {
        //Store the extracted data in form of List of individual row as List<Map<Col, value>>
        List<Map<String, String>> result = new ArrayList<>();

        //Open the excel file using OPCPackage for more info
        try (OPCPackage pkg = OPCPackage.open(inputStream)) {

            //Creates an XSSFReader to read the XML data from the Excel file.
            XSSFReader reader = new XSSFReader(pkg);
            SharedStrings sst = reader.getSharedStringsTable();
            List<Future<Map<String, String>>> futures = new ArrayList<>();

            XMLReader parser = fetchSheetParser(sst, futures, executorService);
            try (InputStream sheet = reader.getSheetsData().next()) {
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            for (Future<Map<String, String>> future : futures) {
                result.add(future.get());
            }

        } catch (RowLimitExceededException rowLimitExceededException){
            LOGGER.error("Row Limit Completed");
        }catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return result.toString();
    }

    private XMLReader fetchSheetParser(SharedStrings sst, List<Future<Map<String, String>>> futures, ExecutorService executorService) {
        try {
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            parser.setContentHandler(new SheetHandler(sst, futures, executorService));
            return parser;
        } catch (SAXException | ParserConfigurationException saxException) {
            throw new RuntimeException(saxException);
        }
    }


    private static String extractTextFromPDF(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private @NotNull String doDocumentEmbeddingAndIngestionProcess(List<Document> documentList) {
        try {
            List<TextSegment> textSegmentList = new DocumentByWordSplitter(1000, 50)
                    .splitAll(documentList);
            List<Embedding> embeddingList = embeddingModel.embedAll(textSegmentList).content();
            LOGGER.info("Embedding Completed for the Text Segments");
            embeddingStore.addAll(embeddingList, textSegmentList);
            LOGGER.info("Stored all the embeddings into Embedding Store(Vector Database)");
            return INGESTION_SUCCESS_RESPONSE;
        }catch (Exception e){
            throw new RuntimeException("Error occurred during embedding and ingestion process ",e);
        }
    }

    private String findTheUrlContent(String url) {
        try {
            ResponseEntity<String> data =  restTemplate.getForEntity(url,String.class);
            org.jsoup.nodes.Document document = Jsoup.parse(Objects.requireNonNull(data.getBody()));
            return document.body().text().substring(0,2000);
        }catch (Exception e){
            throw new RuntimeException("Error occurred while fetching the url Content {}",e);
        }

    }
    
    
}
