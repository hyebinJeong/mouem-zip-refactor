package org.scoula.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.scoula.register.domain.dto.RegisterDTO;

@Mapper
public interface RegisterMapper {
    void insertRegister(RegisterDTO register);
    RegisterDTO selectByRegisterId(Integer registerId);
}
