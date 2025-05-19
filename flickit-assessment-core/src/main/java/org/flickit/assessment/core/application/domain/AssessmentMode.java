package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentMode {

    QUICK,
    ADVANCED;

    public static AssessmentMode valueOfById(Integer id) {
        return Stream.of(AssessmentMode.values())
            .filter(x -> Objects.equals(x.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public int getId() {
        return this.ordinal();
    }
}
