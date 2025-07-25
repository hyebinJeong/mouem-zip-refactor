package org.scoula.register.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterAnalysisResponse {
    private List<MortgageDTO> mortgageInfos;                                // 근저당권
    private List<SeizureDTO> seizureInfos;                                  // 압류
    private List<ProvisionalSeizureDTO> provisionalSeizureInfos;            // 가압류
    private List<AuctionDTO> auctionInfos;                                  // 경매
    private List<ProvisionalRegistrationDTO> provisionalRegistrationInfos;  // 가등기
    private List<InjunctionDTO> injunctionInfos;                            // 가처분
    private List<JeonseRightDTO> jeonseRightInfos;                          // 전세권설정
    private List<TrustDTO> trustInfos;                                      // 신탁등기
}
