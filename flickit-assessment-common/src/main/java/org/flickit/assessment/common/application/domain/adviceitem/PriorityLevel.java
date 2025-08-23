package org.flickit.assessment.common.application.domain.adviceitem;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PriorityLevel {

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
        return MessageBundle.message(getClass().getSimpleName()+ "_" + name());
    }

    public String getTitle(Locale locale) {
        return MessageBundle.message(getClass().getSimpleName() + "_" + name(), locale);
    }

    public static PriorityLevel valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < PriorityLevel.values().length;
    }
}
