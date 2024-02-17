package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EvidenceType {

    POSITIVE,
    NEGATIVE;

    public Integer getId() { return ordinal() + 1; }

    public String getTitle() { return name().toLowerCase();}

    public static EvidenceType valueOfById(Integer id) {
        return Stream.of(EvidenceType.values())
            .filter(x -> x.getId().equals(id))
            .findAny()
            .orElse(null);
    }
}
