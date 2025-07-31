package org.scoula.finalreport.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.finalreport.dto.FinalReportDTO;

@Mapper
public interface FinalReportMapper {

    FinalReportDTO getFinalReport(@Param("reportId") Long reportId);
}
