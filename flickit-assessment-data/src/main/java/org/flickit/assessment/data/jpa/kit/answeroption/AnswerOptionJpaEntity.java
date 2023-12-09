package org.flickit.assessment.data.jpa.kit.answeroption;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_answertemplate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerOptionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_answertemplate_id_seq")
    @SequenceGenerator(name = "baseinfo_answertemplate_id_seq", sequenceName = "baseinfo_answertemplate_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "caption", nullable = false)
    private String title;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "index", nullable = false)
    private Integer index;
}
