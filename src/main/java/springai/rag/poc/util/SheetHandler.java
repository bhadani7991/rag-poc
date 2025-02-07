package springai.rag.poc.util;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import springai.rag.poc.exception.RowLimitExceededException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class SheetHandler extends DefaultHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(SheetHandler.class);

    private final SharedStrings sst;
    private final List<Future<Map<String, String>>> futures;
    private final ExecutorService executorService;
    private List<String> headers = new ArrayList<>();
    private final List<String> currentRow = new ArrayList<>();
    private boolean isHeaderRow = true;
    private boolean isString;
    private String lastContents;
    private int rowCount = 0;
    private static final int ROW_LIMIT = 10;

    public SheetHandler(SharedStrings sst, List<Future<Map<String, String>>> futures, ExecutorService executorService) {
        this.sst = sst;
        this.futures = futures;
        this.executorService = executorService;
    }

    @Override
    public void startElement(String uri, String localName, String name, org.xml.sax.Attributes attributes) throws SAXException {
        if (name.equals("c")) {
            String t = attributes.getValue("t");
            isString = t != null && t.equals("s");
        }
        lastContents = ""; // Reset for each element
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }


    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (isString && name.equals("v")) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getItemAt(idx).getString()).toString();
        }

        if (name.equals("v") || name.equals("t")) {
            currentRow.add(lastContents.trim());
        }

        if (name.equals("row")) {
            if (isHeaderRow) {
                headers = new ArrayList<>(currentRow);
                isHeaderRow = false;
            } else {
                rowCount++;
                if (rowCount > ROW_LIMIT) {
                    throw new RowLimitExceededException("Completed Processing "+ROW_LIMIT);
                }

                List<String> rowCopy = new ArrayList<>(currentRow);
                futures.add(executorService.submit(() -> processRow(rowCopy, headers)));
            }
            currentRow.clear();
        }
    }

    /**
     *
     * @param row - individual row
     * @param headers - header of excel sheet
     * @return - Map<String, String>  - Map<col,value>
     */
    private Map<String, String> processRow(List<String> row, List<String> headers) {
        Map<String, String> rowData = new LinkedHashMap<>();
        for (int i = 0; i < row.size(); i++) {
            String header = i < headers.size() ? headers.get(i) : "Column" + i;
            rowData.put(header, row.get(i));
        }
        LOGGER.info("One Row Data {}", rowData);
        return rowData;
    }

}


