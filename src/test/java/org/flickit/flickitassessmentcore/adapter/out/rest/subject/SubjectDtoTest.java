package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.application.domain.Subject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubjectDtoTest {

    @Test
    void dtoToDomain() {
        List<QualityAttributeDto> qaDtoList = List.of(
            new QualityAttributeDto(123L, 1),
            new QualityAttributeDto(234L, 2)
        );
        SubjectDto dto = new SubjectDto(123L, qaDtoList);

        Subject domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertNotNull(domain.getQualityAttributes());
        assertEquals(dto.qualityAttributes().size(), domain.getQualityAttributes().size());

        for (int i = 0; i < qaDtoList.size(); i++) {
            assertEquals(qaDtoList.get(i).id(), domain.getQualityAttributes().get(i).getId());
            assertEquals(qaDtoList.get(i).weight(), domain.getQualityAttributes().get(i).getWeight());
        }
    }
}
