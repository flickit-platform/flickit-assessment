package org.flickit.assessment.advice.application.domain.adviceitem;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ImpactLevel {

    LOW,
    MEDIUM,
    HIGH;

    public int getId() {
        return this.ordinal();
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return MessageBundle.message(getClass().getSimpleName() + "_" + name());
    }

    public static ImpactLevel valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < ImpactLevel.values().length;
    }
}
