package org.flickit.assessment.advice.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Assessment {

    private final UUID id;
    private final String title;
    private final String shortTitle;
}
