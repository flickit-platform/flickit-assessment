package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.flickit.assessment.common.application.MessageBundle;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentMode {

    QUICK,
    ADVANCED;

    public String getCode() {
        return this.name();
    }

    public String getTitle() {
        return MessageBundle.message(getClass().getSimpleName() + "_" + name());
    }

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
