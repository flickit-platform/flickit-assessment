package org.flickit.assessment.advice.adapter.out.persistence.advice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessmentadvice.CreateAdvicePort.Param;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceMapper {

    public static AdviceJpaEntity mapToEntity(Param param) {
        return new AdviceJpaEntity(
            null,
            param.assessmentResultId(),
            param.createdBy(),
            param.creationTime(),
            param.lastModificationTime()
        );
    }
}
