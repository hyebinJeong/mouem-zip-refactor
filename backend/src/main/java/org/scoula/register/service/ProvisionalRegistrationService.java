package org.scoula.register.service;

import org.scoula.register.domain.dto.ProvisionalRegistrationDTO;

import java.util.List;

public interface ProvisionalRegistrationService {
    List<ProvisionalRegistrationDTO> extractProvisionalRegistrationInfos(List<List<String>> tableData);
}
