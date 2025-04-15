package org.flickit.assessment.kit.application.port.out.kitlanguage;

public interface CreateKitLanguagePort {

    void persist(long kitId, int langId);
}
