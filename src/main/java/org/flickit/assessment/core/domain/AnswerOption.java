package org.flickit.assessment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AnswerOption {
    private Long id;
    private Question question;
    private String caption;
    private Integer value;
    private Integer index;

    public AnswerOption(Long id) {
        this.id = id;
    }
}
