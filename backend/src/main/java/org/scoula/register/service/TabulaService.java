package org.scoula.register.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.scoula.register.domain.RegistryRating;
import org.scoula.register.domain.dto.RegisterAnalysisResponse;
import org.scoula.register.domain.dto.RegisterDTO;
import org.scoula.register.mapper.RegisterMapper;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class TabulaService {
    private final RegisterMapper registerMapper;
    private final ObjectMapper objectMapper;
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

    public void saveAnalysis(int userId, String address, RegisterAnalysisResponse response, String registryName, RegistryRating registryRating, boolean status, String fileName) throws Exception {
        String risks = objectMapper.writeValueAsString(response);

        RegisterDTO dto = new RegisterDTO();
        dto.setUserId(userId);
        dto.setAddress(address);
        dto.setRisks(risks);
        dto.setRegistryName(registryName);
        dto.setRegistryRating(registryRating);
        dto.setStatus(status);
        dto.setFileName(fileName);

        registerMapper.insertRegister(dto);
    }
}
