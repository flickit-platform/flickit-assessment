package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.flickit.assessment.common.application.MessageBundle;

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

    public static AssessmentMode valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentMode.values().length;
    }

    public int getId() {
        return this.ordinal();
    }
}
