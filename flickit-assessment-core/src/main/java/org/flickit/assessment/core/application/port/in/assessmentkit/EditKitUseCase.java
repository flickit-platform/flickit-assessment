package org.flickit.assessment.core.application.port.in.assessmentkit;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.core.common.SelfValidating;

public interface EditKitUseCase {

    void edit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        Long kitId;
        String content;

        public Param(Long kitId, String content) {
            this.kitId = kitId;
            this.content = content;
            this.validateSelf();
        }
    }
}
