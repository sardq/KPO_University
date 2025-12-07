package demo.models;

public enum GradeEnum {
    NONE("-"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SICK("Б"),
    ABSENT("О"),
    VALID_REASON("УП");

    private final String code;

    GradeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GradeEnum fromCode(String code) {
        for (GradeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Недопустимое значение оценки: " + code);
    }
}
