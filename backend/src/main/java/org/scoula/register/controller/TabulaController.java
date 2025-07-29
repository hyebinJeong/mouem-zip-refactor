package org.scoula.register.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.register.domain.RegistryRating;
import org.scoula.register.domain.dto.*;
import org.scoula.register.service.*;
import org.scoula.register.util.RegisterRatingEvaluator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/safety-check")
public class TabulaController {

    private final TabulaService tabulaService;
    private final AwsS3Service awsS3Service;
    private final MortgageServiceImpl mortgageServiceImpl;
    private final SeizureServiceImpl seizureServiceImpl;
    private final ProvisionalSeizureServiceImpl provisionalSeizureServiceImpl;
    private final AuctionServiceImpl auctionServiceImpl;
    private final ProvisionalRegistrationServiceImpl provisionalRegistrationServiceImpl;
    private final InjunctionServiceImpl injunctionServiceImpl;
    private final JeonseRightServiceImpl jeonseRightServiceImpl;
    private final TrustServiceImpl trustServiceImpl;

    @PostMapping
    public ResponseEntity<?> analyzeRegistry(@RequestParam("file") MultipartFile file) {
        try {
            // S3 업로드
            String uploadedFileName = awsS3Service.uploadFile(file);
            String fileUrl = awsS3Service.getFileUrl(uploadedFileName);

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

            // 임시 값
            int userId = 1;
            String address = "서울시 강남구 역삼동 123-45";
            String registryName = "역삼동 kb오피스텔";

            // 위험 등급 평가
            RegistryRating registryRating = RegisterRatingEvaluator.evaluateRiskLevel(response);
            boolean status = false;

            tabulaService.saveAnalysis(userId, address, response, registryName, registryRating, status, uploadedFileName);

            Map<String, Object> result = new HashMap<>();
            result.put("analysis", response);
            result.put("fileName", uploadedFileName);
            result.put("fileUrl", fileUrl);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}