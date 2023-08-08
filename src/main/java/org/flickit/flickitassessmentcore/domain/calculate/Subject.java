package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Subject {

    private final Long id;
    private List<QualityAttribute> qualityAttributes;

}
