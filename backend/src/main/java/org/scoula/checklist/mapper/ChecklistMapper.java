package org.scoula.checklist.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChecklistMapper {
    void insertChecklist(@Param("registryId") int registryId,
                         @Param("userId") int userId,
                         @Param("checkedJson") String checkedJson,
                         @Param("checklistRating") String checklistRating);
}
