package org.flickit.assessment.common.application.domain.kit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.util.SpringUtil;

import java.util.List;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KitLanguage {

    EN, FA;

    public String getTitle() {
        return MessageBundle.message(getClass().getSimpleName() + "_" + name());
    }

    public int getId() {
        return this.ordinal();
    }

    public String getCode() {
        return this.name();
    }

    public static KitLanguage valueOfById(int id) {
        if (!isValidId(id))
            return getDefault();
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < KitLanguage.values().length;
    }

    public static KitLanguage getDefault() {
        return EN;
    }

    public static KitLanguage getEnum(String name) {
        var lang = EnumUtils.getEnum(KitLanguage.class, name, getDefault());
        return isSupported(lang) ? lang : getDefault();
    }

    public static List<KitLanguage> getSupportedLanguages() {
        var appSpecProperties = SpringUtil.getBean(AppSpecProperties.class);
        return appSpecProperties.getSupportedKitLanguages().stream().toList();
    }

    private static boolean isSupported(KitLanguage language) {
        var appSpecProperties = SpringUtil.getBean(AppSpecProperties.class);
        return appSpecProperties.getSupportedKitLanguages().contains(language);
    }
}
