package org.flickit.assessment.common.application.domain.crud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sort {
    ASC("asc"),
    DESC("desc");

    private final String title;

    public static final Sort DEFAULT = ASC;
}

