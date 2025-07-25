package org.scoula.register.service;

import org.scoula.register.domain.dto.JeonseRightDTO;

import java.util.List;

public interface JeonseRightService {
    List<JeonseRightDTO> extractJeonseRightInfos(List<List<String>> tableData);
}
