package org.scoula.jeonseRate.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.jeonseRate.domain.JeonseAnalysisVO;

@Mapper
public interface JeonseAnalysisMapper {
    void insertJeonseAnalysis(JeonseAnalysisVO analysis);
    int findJeonseRatioByRegisterId(int registerId);
}
