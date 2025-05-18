package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentMode {

    QUICK,
    ADVANCED;

    public int getId() {
        return this.ordinal();
    }
}
