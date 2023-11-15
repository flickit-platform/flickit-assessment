package org.flickit.assessment.data.jpa.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findByAssessmentKitId(Long assessmentKitId);

    @Modifying
    @Query("UPDATE MaturityLevelJpaEntity m SET " +
        "m.title = :title, " +
        "m.index = :index, " +
        "m.value = :value " +
        "WHERE m.code = :code ")
    void update(@Param(value = "code") String code,
                @Param(value = "title") String title,
                @Param(value = "index") int index,
                @Param(value = "value") int value);

    MaturityLevelJpaEntity findByTitle(String title);
}
