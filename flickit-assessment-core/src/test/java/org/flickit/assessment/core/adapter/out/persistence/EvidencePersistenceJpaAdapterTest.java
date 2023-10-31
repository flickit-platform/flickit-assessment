package org.flickit.assessment.core.adapter.out.persistence;

import org.flickit.assessment.core.adapter.out.persistence.evidence.EvidenceJpaRepository;
import org.flickit.assessment.core.adapter.out.persistence.evidence.EvidenceMapper;
import org.flickit.assessment.core.adapter.out.persistence.evidence.EvidencePersistenceJpaAdapter;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
@Import({EvidencePersistenceJpaAdapter.class, EvidenceMapper.class})
class EvidencePersistenceJpaAdapterTest {

    @Autowired
    private EvidencePersistenceJpaAdapter persistenceJpaAdapter;

    @Autowired
    private EvidenceJpaRepository repository;

    @Disabled
    @Test
    void loadEvidencesByQuestionIdAndAssessmentIdTest() {
        var assessmentId = UUID.randomUUID();
        // Create an assessment
        var questionId = 1L;
        var size = 10;
        var page = 0;

        var param = new CreateEvidencePort.Param("desc1", LocalDateTime.now(), LocalDateTime.now(), 1L, assessmentId, questionId);
        var entity = EvidenceMapper.mapCreateParamToJpaEntity(param);
        repository.save(entity);

//        var evidences = persistenceJpaAdapter.loadEvidencesByQuestionIdAndAssessmentId(questionId, assessmentId, page, size);

//        assertEquals(1, evidences.getItems().size());
    }
}
