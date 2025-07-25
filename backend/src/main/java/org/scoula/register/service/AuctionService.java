package org.scoula.register.service;

import org.scoula.register.domain.dto.AuctionDTO;

import java.util.List;

public interface AuctionService {
    List<AuctionDTO> extractAuctionInfos(List<List<String>> tableData);
}
