package org.scoula.finalreport.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.finalreport.dto.FinalReportRawDTO;

@Mapper
public interface FinalReportMapper {
    FinalReportRawDTO getFinalReport(@Param("reportId") Long reportId);
}
