package org.flickit.assessment.data.jpa.kit.question;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@IdClass(QuestionJpaEntity.EntityId.class)
@Table(name = "fak_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_question_id_seq")
    @SequenceGenerator(name = "fak_question_id_seq", sequenceName = "fak_question_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "hint")
    private String hint;

    @Column(name = "may_not_be_applicable", nullable = false)
    private Boolean mayNotBeApplicable;

    @Column(name = "advisable", nullable = false)
    private Boolean advisable;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private long id;
        private long kitVersionId;
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {

        public static final String INDEX = "index";
    }
}
