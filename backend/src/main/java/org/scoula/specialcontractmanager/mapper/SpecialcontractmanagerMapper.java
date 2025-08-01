package org.scoula.specialcontractmanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.specialcontractmanager.domain.SpecialcontractmanagerVO;

@Mapper
public interface SpecialcontractmanagerMapper {
    List<SpecialcontractmanagerVO> selectAll();

    SpecialcontractmanagerVO selectById(@Param("id") Integer id);

    int insert(SpecialcontractmanagerVO vo);

    int update(SpecialcontractmanagerVO vo);

    int delete(@Param("id") Integer id);
}
