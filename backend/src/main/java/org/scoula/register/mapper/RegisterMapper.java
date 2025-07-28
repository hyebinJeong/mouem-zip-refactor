package org.scoula.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.register.domain.dto.RegisterDTO;

@Mapper
public interface RegisterMapper {
    void insertRegister(RegisterDTO register);
}
