package org.scoula.register.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.register.domain.dto.*;
import org.scoula.register.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/safety-check")
public class TabulaController {

    private final TabulaService tabulaService;
    private final MortgageServiceImpl mortgageServiceImpl;
    private final SeizureServiceImpl seizureServiceImpl;
    private final ProvisionalSeizureServiceImpl provisionalSeizureServiceImpl;
    private final AuctionServiceImpl auctionServiceImpl;
    private final ProvisionalRegistrationServiceImpl provisionalRegistrationServiceImpl;
    private final InjunctionServiceImpl injunctionServiceImpl;
    private final JeonseRightServiceImpl jeonseRightServiceImpl;
    private final TrustServiceImpl trustServiceImpl;

//    @PostMapping
//    public ResponseEntity<List<List<String>>> extractTableFromPdf(@RequestParam("file") MultipartFile file) {
//        try {
//            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
//            return ResponseEntity.ok(table); // JSON 형태로 표 데이터 반환
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @PostMapping
    public ResponseEntity<RegisterAnalysisResponse> analyzeRegistry(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());

            RegisterAnalysisResponse response = new RegisterAnalysisResponse();
            response.setMortgageInfos(mortgageServiceImpl.extractMortgageInfos(table));
            response.setSeizureInfos(seizureServiceImpl.extractSeizureInfos(table));
            response.setProvisionalSeizureInfos(provisionalSeizureServiceImpl.extractProvisionalSeizureInfos(table));
            response.setAuctionInfos(auctionServiceImpl.extractAuctionInfos(table));
            response.setProvisionalRegistrationInfos(provisionalRegistrationServiceImpl.extractProvisionalRegistrationInfos(table));
            response.setInjunctionInfos(injunctionServiceImpl.extractInjunctions(table));
            response.setJeonseRightInfos(jeonseRightServiceImpl.extractJeonseRightInfos(table));
            response.setTrustInfos(trustServiceImpl.extractTrustInfos(table));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}