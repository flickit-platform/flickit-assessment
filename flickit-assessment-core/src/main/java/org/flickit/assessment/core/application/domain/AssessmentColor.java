package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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

    @JsonIgnore
    public String getTitle() {
        return name().toLowerCase();
    }

    public static AssessmentColor valueOfById(int id) {
        return Stream.of(AssessmentColor.values())
            .filter(x -> x.getId() == id)
            .findAny()
            .orElse(null);
    }

    public static int getValidId(Integer id) {
        return AssessmentColor.isValidId(id) ? id : AssessmentColor.getDefault().getId();
    }

    private static boolean isValidId(int id) {
        return id > 0 && id < AssessmentColor.values().length + 1;
    }

    public static AssessmentColor getDefault() {
        return MIDNIGHT_GREEN;
    }
}
