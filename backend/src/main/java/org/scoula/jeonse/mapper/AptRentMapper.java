package org.scoula.jeonse.mapper;

import org.scoula.jeonse.domain.AptRentRow;
import org.scoula.jeonse.dto.AptRentDto;

import java.util.List;
import java.util.stream.Collectors;

public class AptRentMapper {

    public static AptRentDto toDto(AptRentRow r) {
        if (r == null) return null;
        return AptRentDto.builder()
                .aptNm(r.getAptNm())
                .buildYear(r.getBuildYear())
                .dealYear(r.getDealYear())
                .dealMonth(r.getDealMonth())
                .dealDay(r.getDealDay())
                .deposit(r.getDeposit())
                .monthlyRent(r.getMonthlyRent())
                .excluUseAr(r.getExcluUseAr())
                .floor(r.getFloor())
                .jibun(r.getJibun())
                .sggCd(r.getSggCd())
                .umdNm(r.getUmdNm())
                .build();
    }

    public static List<AptRentDto> toDtoList(List<AptRentRow> rows) {
        return rows == null ? List.of() : rows.stream().map(AptRentMapper::toDto).collect(Collectors.toList());
    }
}