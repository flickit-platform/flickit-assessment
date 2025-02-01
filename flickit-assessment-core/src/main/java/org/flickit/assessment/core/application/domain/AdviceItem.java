package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AdviceItem {

    private final UUID id;
    private final String title;
    private final String description;
    private final String cost;
    private final String priority;
    private final String impact;
}
