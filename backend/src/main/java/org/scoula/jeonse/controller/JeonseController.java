// org/scoula/jeonse/controller/JeonseController.java
package org.scoula.jeonse.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.jeonse.dto.AptRentDto;
import org.scoula.jeonse.dto.ApiResult;
import org.scoula.jeonse.dto.JeonseRatioDto;
import org.scoula.jeonse.service.JeonseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/jeonse", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JeonseController {

    private final JeonseService service;

    // ✅ 기존: 전월세 표 조회
    // GET /api/jeonse/apt?lawd=11680&ym=202110&umd=대치동
    @GetMapping("/apt")
    public ResponseEntity<ApiResult<AptRentDto>> getAptRent(
            @RequestParam String lawd,
            @RequestParam(name = "ym") String yyyymm,
            @RequestParam(required = false, defaultValue = "") String umd
    ) {
        List<AptRentDto> rows = service.getAptRent(lawd, yyyymm, umd.isBlank() ? null : umd);
        return ResponseEntity.ok(ApiResult.of(rows.size(), rows));
    }

    // ✅ 특정 달(yyyymm)의 전세가율 리스트
    // GET /api/jeonse/ratio?lawd=11680&ym=202110&umd=대치동&apt=은마
    @GetMapping("/ratio")
    public ResponseEntity<ApiResult<JeonseRatioDto>> getJeonseRatio(
            @RequestParam String lawd,
            @RequestParam(name = "ym") String yyyymm,
            @RequestParam(required = false, defaultValue = "") String umd,
            @RequestParam(required = false, defaultValue = "") String apt
    ) {
        List<JeonseRatioDto> rows = service.getJeonseRatio(
                lawd,
                yyyymm,
                umd.isBlank() ? null : umd,
                apt.isBlank() ? null : apt
        );
        return ResponseEntity.ok(ApiResult.of(rows.size(), rows));
    }

    // ✅ 최근 4년(48개월) 중에서 "무작위 8개 월" 샘플 전세가율(월평균)
    // GET /api/jeonse/ratio/recent?lawd=11680&umd=대치동&apt=은마
    @GetMapping("/ratio/recent")
    public ResponseEntity<ApiResult<JeonseRatioDto>> getJeonseRatioRecent(
            @RequestParam String lawd,
            @RequestParam(required = false, defaultValue = "") String umd,
            @RequestParam(required = false, defaultValue = "") String apt
    ) {
        List<JeonseRatioDto> rows = service.getJeonseRatioRecent(
                lawd,
                umd.isBlank() ? null : umd,
                apt.isBlank() ? null : apt
        );
        return ResponseEntity.ok(ApiResult.of(rows.size(), rows));
    }
}