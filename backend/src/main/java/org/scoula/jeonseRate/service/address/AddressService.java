package org.scoula.jeonseRate.service.address;

import org.scoula.jeonseRate.dto.AddressInfoDTO;

public interface AddressService {
    // 사용자가 입력한 키워드(주소)를 기반으로 주소 정보 조회(법정동 코드, 지번, 건물명)
    AddressInfoDTO lookupAddress(String keyword);
}
