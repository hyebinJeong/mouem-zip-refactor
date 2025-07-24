package org.scoula.register.service;

import org.scoula.register.domain.dto.InjunctionDTO;

import java.util.List;

public interface InjunctionService {
    List<InjunctionDTO> extractInjunctions(List<List<String>> tableData);
}
