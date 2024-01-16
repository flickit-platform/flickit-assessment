package org.flickit.assessment.kit.application.port.out.assessmentkitdsl;

public interface UpdateKitDslPort {

    void update(Param param);

    record Param(Long id, Long kitId) {
    }
}
