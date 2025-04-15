package org.flickit.assessment.kit.application.port.out.kitlanguage;

public interface CreateKitLanguagePort {

    void persist(Long kitId, Integer langId);
}
