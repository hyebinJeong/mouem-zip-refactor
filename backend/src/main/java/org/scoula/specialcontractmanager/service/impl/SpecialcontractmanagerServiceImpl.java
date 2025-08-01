package org.scoula.specialcontractmanager.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.scoula.specialcontractmanager.domain.SpecialcontractmanagerVO;
import org.scoula.specialcontractmanager.mapper.SpecialcontractmanagerMapper;
import org.scoula.specialcontractmanager.service.SpecialcontractmanagerService;

@Service
public class SpecialcontractmanagerServiceImpl implements SpecialcontractmanagerService {

    private final SpecialcontractmanagerMapper mapper;

    public SpecialcontractmanagerServiceImpl(SpecialcontractmanagerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SpecialcontractmanagerVO> getAllSpecialClauses() {
        return mapper.selectAll();
    }

    @Override
    public SpecialcontractmanagerVO getSpecialClauseById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public boolean addSpecialClause(SpecialcontractmanagerVO vo) {
        // 중요도에 따라 색상 자동 설정
        vo.setImportanceColor(getColorForImportance(vo.getImportance()));
        return mapper.insert(vo) == 1;
    }

    @Override
    public boolean updateSpecialClause(SpecialcontractmanagerVO vo) {
        vo.setImportanceColor(getColorForImportance(vo.getImportance()));
        return mapper.update(vo) == 1;
    }

    @Override
    public boolean deleteSpecialClause(Integer id) {
        return mapper.delete(id) == 1;
    }

    private String getColorForImportance(String importance) {
        return switch (importance) {
            case "높음" -> "#FF0000";
            case "보통" -> "#FFA500";
            case "낮음" -> "#00FF00";
            default -> "#000000";
        };
    }
}
