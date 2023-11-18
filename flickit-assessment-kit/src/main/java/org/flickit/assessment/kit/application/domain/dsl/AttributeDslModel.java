package org.flickit.assessment.kit.application.domain.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AttributeDslModel extends BaseDslModel {

    private String subjectCode;
    private Integer weight;
}
