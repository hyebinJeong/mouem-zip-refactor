package org.scoula.checklist.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.checklist.domain.dto.ChecklistDTO;

import java.util.List;


@Mapper
public interface ChecklistMapper {
    void insertChecklist(@Param("registryId") int registryId,
                         @Param("userId") int userId,
                         @Param("checkedJson") String checkedJson,
                         @Param("checklistRating") String checklistRating);

    List<ChecklistDTO> findByUserId(@Param("userId") int userId);

}
