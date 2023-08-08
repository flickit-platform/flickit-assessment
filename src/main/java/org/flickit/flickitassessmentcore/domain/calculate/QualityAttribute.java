package org.flickit.flickitassessmentcore.domain.calculate;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QualityAttribute {

    private final Long id;
    private final Integer weight;

    /** This field is set when required (e.g. calculate) */
    @Nullable
    private final List<Question> questions;
}
