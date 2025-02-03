//package springai.rag.poc.util;
//
//import dev.langchain4j.data.document.Document;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class DocumentProcessor {
//
//    /**
//     * Utility method to get the Document from the Multipart file
//     * based on different types.
//     * @param file - document uploaded by user
//     * @return Document - {@link Document}
//     */
//    private static Document processFile(MultipartFile file) {
//        String contentType = file.getContentType();
//        try {
//            InputStream inputStream = file.getInputStream();
//
//            if (contentType == null) {
//                throw new IOException("Unknown file type: " + file.getOriginalFilename());
//            }
//
//            switch (contentType){
//                case "text/plain" -> Document.from(new String(inputStream.readAllBytes(),
//                        StandardCharsets.UTF_8));
//                case "application/pdf" -> Document.from(extractTextFromPdf(inputStream));
//                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->
//                        Document.from(extractTextFromExcel(inputStream));
//                case "application/msword" -> Document.from(extractTextFromDoc(inputStream));
//                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
//                        Document.from(extractTextFromDocx(inputStream));
//                default -> throw new IOException("Unsupported type");
//
//            }
//            return Document.from("");
//        }catch (IOException ioException){
//            throw new RuntimeException("Error occurred while processing documents",ioException);
//        }
//    }
//
//    //  Extract text from PDF using Apache PDFBox
//    private static String extractTextFromPdf(InputStream inputStream) {
//        try (PDDocument document = PDDocument.load(inputStream)) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            return stripper.getText(document);
//        }catch(IOException e){
//            throw new RuntimeException("Error occurred while Extracting text from PDF ",e);
//        }
//    }
//
//    // ✅ Extract text from Excel (.xlsx) using Apache POI
//    private static String extractTextFromExcel(InputStream inputStream) throws IOException {
//        StringBuilder text = new StringBuilder();
//        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
//            for (Sheet sheet : workbook) {
//                for (Row row : sheet) {
//                    for (Cell cell : row) {
//                        text.append(cell.toString()).append(" ");
//                    }
//                    text.append("\n");
//                }
//            }
//        }
//        return text.toString();
//    }
//
//    // ✅ Extract text from .doc (Word 97-2003) using Apache POI
//    private static String extractTextFromDoc(InputStream inputStream) throws IOException {
//        try (WordExtractor extractor = new WordExtractor(inputStream)) {
//            return extractor.getText();
//        }
//    }
//
//    // ✅ Extract text from .docx (Word 2007+) using Apache POI
//    private static String extractTextFromDocx(InputStream inputStream) throws IOException {
//        try (XWPFDocument document = new XWPFDocument(inputStream);
//             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
//            return extractor.getText();
//        }
//    }
//}
