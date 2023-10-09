package org.flickit.flickitassessmentcore.adapter.out.persistence;

import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidenceJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidenceMapper;
import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidencePersistenceJpaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({EvidencePersistenceJpaAdapter.class, EvidenceMapper.class})
class EvidencePersistenceJpaAdapterTest {

    @Autowired
    private EvidencePersistenceJpaAdapter persistenceJpaAdapter;

    @Autowired
    private EvidenceJpaRepository repository;

    @Test
    void loadEvidencesByQuestionIdAndAssessmentIdTest() {
        var assessmentId = UUID.randomUUID();
        var questionId = 1L;
        var size = 10;
        var page = 0;
        var evidences = persistenceJpaAdapter.loadEvidencesByQuestionIdAndAssessmentId(questionId, assessmentId, page, size);

        assertEquals(0, evidences.getItems().size());
    }
}
