package org.scoula.contract.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.contract.domain.ContractDTO;
import org.scoula.contract.service.ContractService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final JwtProcessor jwtProcessor;
    @PostMapping
    public ResponseEntity<?> saveContract(@RequestBody ContractDTO contractDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            int userId = Integer.parseInt(jwtProcessor.getUserId(token));
            contractDTO.setUserId(userId); // ✅ JWT로 세팅

            if (contractDTO.getSpecialClauses() == null) {
                contractDTO.setSpecialClauses(new ArrayList<>()); // ✅ null 방어
            }

            contractService.saveContract(contractDTO);
            return ResponseEntity.ok(contractDTO.getContractId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("계약서 저장 실패");
        }

    }

    @GetMapping("/list")
    public ResponseEntity<List<ContractDTO>> getUserContractList(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        int userId = Integer.parseInt(jwtProcessor.getUserId(token));
        return ResponseEntity.ok(contractService.getContractListByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessChecker.canViewContract(#id, authentication)")
    public ResponseEntity<ContractDTO> getContractDetail(@PathVariable int id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        int userId = Integer.parseInt(jwtProcessor.getUserId(token));

        ContractDTO contract = contractService.getContractById(id);
        if (contract == null || contract.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(contract);
    }

}
