package org.flickit.assessment.data.jpa.kit.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttributeJpaRepository extends JpaRepository<AttributeJpaEntity, Long> {

    List<AttributeJpaEntity> findAllBySubjectId(long subjectId);

    @Modifying
    @Query("UPDATE AttributeJpaEntity a SET " +
        "a.title = :title, " +
        "a.index = :index, " +
        "a.description = :description, " +
        "a.weight = :weight, " +
        "a.lastModificationTime = :lastModificationTime, " +
        "a.subjectId = :subjectId " +
        "WHERE a.id = :id")
    void update(@Param("id") long id,
                @Param("title") String title,
                @Param("index") int index,
                @Param("description") String description,
                @Param("weight") int weight,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("subjectId") long subjectId);

}
