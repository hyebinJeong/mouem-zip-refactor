package org.scoula.jeonseRate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.jeonseRate.dto.JeonseRateDTO;
import org.scoula.jeonseRate.service.LeaseDiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "jeonse-rate-analysis-controller")
@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
@Slf4j
public class LeaseDiagnosisController {

    private final LeaseDiagnosisService leaseDiagnosisService;

    @ApiOperation(value = "Analyze Jeonse Rate", notes = "Analyze the jeonse ratio based on address and deposit")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Analysis success (results in response body)"),
            @ApiResponse(code = 400, message = "Missing address or request data"),
            @ApiResponse(code = 404, message = "No matching real estate deal found")
    })
    @PostMapping("/leasePer")
    public ResponseEntity<?> analyzeLease(@RequestBody JeonseRateDTO request) {
        return leaseDiagnosisService.analyzeLease(request);
    }

    // 예상 전세가율 조회
    @ApiOperation(
            value = "Get Jeonse Rate Result",
            notes = "Retrieve previously saved jeonse rate analysis result using registry ID"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved jeonse rate"),
            @ApiResponse(code = 404, message = "Jeonse rate not found for the given registry ID (data not saved or not analyzed yet)")
    })
    @GetMapping("/result")
    public ResponseEntity<?> getJeonseAnalysisResult(@RequestParam("registerId") int registerId) {
        return leaseDiagnosisService.getJeonseAnalysisResult(registerId);
    }
}
