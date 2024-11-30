package org.flickit.assessment.advice.application.domain.adviceitem;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CostLevel {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String title;

    public String getCode() {
        return name();
    }
}
