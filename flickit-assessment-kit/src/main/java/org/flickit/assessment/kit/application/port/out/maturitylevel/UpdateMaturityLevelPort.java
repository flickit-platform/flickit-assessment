package org.flickit.assessment.kit.application.port.out.maturitylevel;

import java.util.Map;

public interface UpdateMaturityLevelPort {

    void update(Param param);

    record Param(
        String code,
        String title,
        int index,
        int value
    ) {}
}
