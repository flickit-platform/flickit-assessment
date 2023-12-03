package org.flickit.assessment.kit.application.domain.dsl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseDslModel {

    private String code;
    private Integer index;
    private String title;
    private String description;
}
