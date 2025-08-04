package org.scoula.checklist.service;

import org.scoula.checklist.domain.dto.ChecklistDTO;

import java.util.List;

public interface ChecklistService {
    void saveChecklist(ChecklistDTO dto);
}
