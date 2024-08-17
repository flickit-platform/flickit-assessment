package org.flickit.assessment.kit.application.port.out.maturitylevel;

public interface DeleteMaturityLevelPort {

    void delete(Long id, Long kitVersionId);
}
