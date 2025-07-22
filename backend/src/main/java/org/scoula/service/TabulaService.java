package org.scoula.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TabulaService {
    public List<List<String>> extractTableFromPdf(File pdfFile) throws Exception {
        List<List<String>> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(pdfFile);
             PDDocument document = PDDocument.load(fis)) {

            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();

            while (pi.hasNext()) {
                Page page = pi.next();
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();

                    for (List<RectangularTextContainer> cells : rows) {
                        List<String> row = new ArrayList<>();
                        for (RectangularTextContainer cell : cells) {
                            row.add(cell.getText().replace("\r", " "));
                        }
                        result.add(row);
                    }
                }
            }
        }

        return result;
    }
}
