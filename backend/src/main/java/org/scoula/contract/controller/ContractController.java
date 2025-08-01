package org.scoula.contract.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.contract.domain.ContractDTO;
import org.scoula.contract.service.ContractService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public void saveContract(@RequestBody ContractDTO contractDTO) {
        contractService.saveContractWithClauses(contractDTO);
    }
}
