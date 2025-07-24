package org.scoula.register.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.register.domain.dto.*;
import org.scoula.register.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
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

    @PostMapping
    @ResponseBody
    public ResponseEntity<List<List<String>>> extractTableFromPdf(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            return ResponseEntity.ok(table); // JSON 형태로 표 데이터 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/mortgages")
    @ResponseBody
    public ResponseEntity<List<MortgageDTO>> extractMortgages(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<MortgageDTO> mortgages = mortgageServiceImpl.extractMortgageInfos(table);
            return ResponseEntity.ok(mortgages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/seizures")
    @ResponseBody
    public ResponseEntity<List<SeizureDTO>> extractSeizures(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<SeizureDTO> seizures = seizureServiceImpl.extractSeizureInfos(table);
            return ResponseEntity.ok(seizures);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/provisional")
    @ResponseBody
    public ResponseEntity<List<ProvisionalSeizureDTO>> extractProvisionalSeizures(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<ProvisionalSeizureDTO> provisionalSeizure = provisionalSeizureServiceImpl.extractProvisionalSeizureInfos(table);
            return ResponseEntity.ok(provisionalSeizure);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/auction")
    @ResponseBody
    public ResponseEntity<List<AuctionDTO>> extractAuctions(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<AuctionDTO> auction = auctionServiceImpl.extractAuctionInfos(table);
            return ResponseEntity.ok(auction);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/registration")
    @ResponseBody
    public ResponseEntity<List<ProvisionalRegistrationDTO>> extractProvisionalRegistrations(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<ProvisionalRegistrationDTO> provisionalRegistration = provisionalRegistrationServiceImpl.extractProvisionalRegistrationInfos(table);
            return ResponseEntity.ok(provisionalRegistration);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/injunction")
    @ResponseBody
    public ResponseEntity<List<InjunctionDTO>> extractInjunctions(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<InjunctionDTO> injunction = injunctionServiceImpl.extractInjunctions(table);
            return ResponseEntity.ok(injunction);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}