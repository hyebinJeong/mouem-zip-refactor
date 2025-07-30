package org.scoula.jeonseRate.enums;

public enum SafetyGrade {
    판단보류,
    안전,
    보통,
    주의,
    위험;

    public static SafetyGrade fromDeviation(Integer  deviation) {
        if (deviation == null) return 판단보류;

        if (deviation <= -5) return 안전;
        else if (deviation <= 4) return 보통;
        else if (deviation <= 9) return 주의;
        else return 위험;
    }
}
