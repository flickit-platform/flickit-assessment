package org.flickit.assessment.data.jpa.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);

    @Modifying
    @Query("UPDATE MaturityLevelJpaEntity m SET " +
        "m.value = :value, " +
        "m.title = :title, " +
        "m.code = :code " +
        "WHERE m.title = :title OR m.code = :code OR m.value = :value ")
    void update(@Param(value = "title") String title,
                @Param(value = "code") String code,
                @Param(value = "value") int value);

    MaturityLevelJpaEntity findByTitleAndAssessmentKitId(String title, Long assessmentKitId);
}
