package org.scoula.finalreport.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.finalreport.dto.FinalReportInsertDTO;
import org.scoula.finalreport.dto.FinalReportRawDTO;

import java.util.Map;

@Mapper
public interface FinalReportMapper {
    FinalReportRawDTO getFinalReport(@Param("reportId") Long reportId);

    // userId + registryId로 reportId 찾기
    Long findReportIdByUserAndRegistry(@Param("userId") Long userId, @Param("registryId") Long registryId);

    // userId, registryId 저장
//    void insertFinalReport(@Param("userId") Long userId, @Param("registryId") Long registryId);
    void insertFinalReport(FinalReportInsertDTO dto);


}
