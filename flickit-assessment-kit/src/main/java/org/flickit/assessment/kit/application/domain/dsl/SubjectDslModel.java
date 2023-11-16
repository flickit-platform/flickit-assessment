package org.flickit.assessment.kit.application.domain.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SubjectDslModel extends BaseDslModel {

    private Integer weight;
    private List<String> questionnaireCodes;
}
