package org.scoula.jeonse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApiResult<T> {
    private final int count;
    private final List<T> rows;

    public static <T> ApiResult<T> of(int count, List<T> rows) {
        return new ApiResult<>(count, rows);
    }
}