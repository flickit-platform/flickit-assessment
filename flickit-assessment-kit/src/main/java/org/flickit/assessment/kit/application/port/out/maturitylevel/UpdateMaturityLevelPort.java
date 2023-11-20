package org.flickit.assessment.kit.application.port.out.maturitylevel;

public interface UpdateMaturityLevelPort {

    void update(Param param);

    record Param(
        Long kitId,
        String code,
        String title,
        int index,
        int value
    ) {
    }
}
