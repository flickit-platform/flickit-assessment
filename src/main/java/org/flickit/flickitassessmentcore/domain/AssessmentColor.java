package org.flickit.flickitassessmentcore.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssessmentColor {

    CRAYOLA("#EF476F"),
    CORAL("#F78C6B"),
    SUN_GLOW("#FFD166"),
    EMERALD("#06D6A0"),
    BLUE("#118AB2"),
    MIDNIGHT_GREEN("#073B4C");

    private final String code;

    public int getId() {
        return ordinal() + 1;
    }

    public String getTitle() {
        return name().toLowerCase();
    }

    public static AssessmentColor getDefault() {
        return MIDNIGHT_GREEN;
    }

    public static boolean isValidId(int id) {
        return id > 0  && id < AssessmentColor.values().length + 1;
    }
}
