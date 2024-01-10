package org.flickit.assessment.kit.adapter.out.persistence.assessmentkittagkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkittagkit.AssessmentKitTagKitJpaEntity;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateAssessmentKitTagKitPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitTagKitMapper {
    public static AssessmentKitTagKitJpaEntity toJpaEntity(CreateAssessmentKitTagKitPort.Param param) {
        return new AssessmentKitTagKitJpaEntity(null, param.tagId(), param.kitId());
    }
}
