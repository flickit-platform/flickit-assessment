package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentAnalysisType {

    STATIC_CODE_ANALYSIS("Static Code Analysis"),;

    private final String title;

    public String getCode() {
        return name();
    }
}
