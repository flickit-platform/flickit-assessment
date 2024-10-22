package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectQuestionnaireJpaRepository extends JpaRepository<SubjectQuestionnaireJpaEntity, Long> {

    List<SubjectQuestionnaireJpaEntity> findAllByKitVersionId(Long kitVersionId);

    void deleteByKitVersionId(long kitVersionId);
}
