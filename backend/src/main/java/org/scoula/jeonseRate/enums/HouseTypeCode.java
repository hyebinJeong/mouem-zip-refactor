package org.scoula.jeonseRate.enums;

import java.util.Arrays;
import java.util.Optional;

public enum HouseTypeCode {
    APARTMENT("아파트", "01"),
    MULTI_HOUSE("연립다세대", "02"),
    DETACHED_HOUSE("단독주택", "03"),
    OPISTEL("오피스텔", "04");

    private final String name;
    private final String code;

    HouseTypeCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static Optional<String> fromName(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name.equals(name))
                .map(HouseTypeCode::getCode)
                .findFirst();
    }
}
