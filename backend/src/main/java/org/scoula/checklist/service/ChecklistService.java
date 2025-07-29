package org.scoula.checklist.service;

import org.scoula.checklist.domain.dto.ChecklistDTO;

public interface ChecklistService {
    void saveChecklist(ChecklistDTO dto);
}
