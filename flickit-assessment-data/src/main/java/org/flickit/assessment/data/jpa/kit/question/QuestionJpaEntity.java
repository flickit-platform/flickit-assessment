package org.flickit.assessment.data.jpa.kit.question;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_metric_id_seq")
    @SequenceGenerator(name = "baseinfo_metric_id_seq", sequenceName = "baseinfo_metric_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description") // TODO: must be renamed to hint at the same time with database in a changelog
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;

    @Column(name = "may_not_be_applicable", nullable = false)
    private Boolean mayNotBeApplicable;
}
