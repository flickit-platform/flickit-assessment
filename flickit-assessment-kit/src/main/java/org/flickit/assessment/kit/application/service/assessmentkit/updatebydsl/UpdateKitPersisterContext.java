package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl;

import java.util.HashMap;
import java.util.Map;

public class UpdateKitPersisterContext {

    private final Map<String, Object> exchanges = new HashMap<>();

    public void put(String key, Object value) {
        exchanges.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) exchanges.get(key);
    }

    public static final String KEY_MATURITY_LEVELS = "MATURITY_LEVELS";
    public static final String KEY_QUESTIONNAIRES = "QUESTIONNAIRES";
    public static final String KEY_SUBJECTS = "SUBJECTS";
    public static final String KEY_ATTRIBUTES = "ATTRIBUTES";
    public static final String KEY_ANSWER_RANGES = "ANSWER_RANGES";
}
