package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Questionnaire {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    @Setter
    @EqualsAndHashCode.Exclude private List<Question> questions;
    @EqualsAndHashCode.Exclude private final LocalDateTime creationTime;
    @EqualsAndHashCode.Exclude private final LocalDateTime lastModificationTime;

    public static String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }
}
