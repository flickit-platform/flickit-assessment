package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.levelcompetence.LevelCompetenceDto;

import java.util.ArrayList;
import java.util.List;

public class LevelCompetenceDtoMother {

    private static long levelCompetenceId = 134L;

    private static List<LevelCompetenceDto> createCompetences(Long maturityLevelId) {
        List<LevelCompetenceDto> levelCompetenceDtos = new ArrayList<>();
        for (int i = 0; i < maturityLevelId - 1; i++) {
            var levelCompetenceDto = new LevelCompetenceDto(levelCompetenceId++, (i + 1) * 10, Long.valueOf(i));
            levelCompetenceDtos.add(levelCompetenceDto);
        }
        return levelCompetenceDtos;
    }

    private static long id = 134L;

    public static LevelCompetenceDto createCompetence(int value, long maturityLevelId) {
        return new LevelCompetenceDto(id++, value, maturityLevelId);
    }
}
