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
    void insertFinalReport(FinalReportInsertDTO dto);

    // 50일이 지난 리포트의 status를 false로 변경하고, 변경된 레코드 수를 반환
    int expireOldReports();

}
