package org.flickit.assessment.kit.application.domain;

import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MaturityLevel {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final int value;
    @Setter
    private List<MaturityLevelCompetence> competences;

    public static String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }

}
