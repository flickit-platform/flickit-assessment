package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Subject {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    @Setter
    private List<Attribute> attributes;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;

    public static String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }
}
