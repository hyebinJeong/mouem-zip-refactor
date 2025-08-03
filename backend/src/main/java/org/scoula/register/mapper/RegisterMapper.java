package org.scoula.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.scoula.register.domain.dto.RegisterDTO;

import java.util.List;

@Mapper
public interface RegisterMapper {
    void insertRegister(RegisterDTO register);
    RegisterDTO selectByRegisterId(Integer registerId);

    // 사용자별 등기부 분석 목록 조회
    List<RegisterDTO> selectByUserId(Integer userId);

    // 50일 지난 분석 결과 status false로 변경
    void updateOldRegistryStatus();
}
