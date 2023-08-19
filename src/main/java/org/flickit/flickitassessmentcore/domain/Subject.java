package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Subject {

    private final long id;
    private List<QualityAttribute> qualityAttributes;
}
