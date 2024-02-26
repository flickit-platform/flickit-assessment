package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EvidenceType {

    POSITIVE("Positive"),
    NEGATIVE("Negative");

    private final String title;

    public String getCode() {
        return name();
    }
}
