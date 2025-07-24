package org.scoula.register.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TabulaService {
    // 등기부등본 내 표 추출 메서드
    public List<List<String>> extractTable(InputStream inputStream) throws Exception {
        List<List<String>> extractedData = new ArrayList<>();

        try (PDDocument document = PDDocument.load(inputStream)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = extractor.extract();

            while (pi.hasNext()) {
                Page page = pi.next();
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();

                    for (List<RectangularTextContainer> cells : rows) {
                        List<String> rowData = new ArrayList<>();
                        for (RectangularTextContainer cell : cells) {
                            rowData.add(cell.getText().replace("\r", " ").trim());
                        }
                        extractedData.add(rowData);
                    }
                }
            }
        }

        return extractedData;
    }
}
