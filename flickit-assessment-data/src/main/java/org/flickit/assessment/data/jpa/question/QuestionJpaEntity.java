package org.flickit.assessment.data.jpa.question;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.questionnaire.QuestionnaireJpaEntity;

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
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationDate;

    @Column(name = "index")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", referencedColumnName = "id")
    private QuestionnaireJpaEntity questionnaire;

    @Column(name = "may_not_be_applicable", nullable = false)
    private Boolean mayNotBeApplicable;
}
