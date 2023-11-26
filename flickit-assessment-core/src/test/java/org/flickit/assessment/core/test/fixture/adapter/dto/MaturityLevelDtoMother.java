package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.maturitylevel.MaturityLevelDto;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.adapter.dto.LevelCompetenceDtoMother.createCompetence;

public class MaturityLevelDtoMother {

    public static final long LEVEL_ONE_ID = 10;
    public static final long LEVEL_TWO_ID = 20;
    public static final long LEVEL_THREE_ID = 30;
    public static final long LEVEL_FOUR_ID = 40;
    public static final long LEVEL_FIVE_ID = 50;

    public static List<MaturityLevelDto> allLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive());
    }

    public static MaturityLevelDto levelOne() {
        return new MaturityLevelDto(LEVEL_ONE_ID, 1, 1,
            List.of());
    }

    public static MaturityLevelDto levelTwo() {
        return new MaturityLevelDto(LEVEL_TWO_ID, 2, 2,
            List.of(createCompetence(60, LEVEL_TWO_ID)));
    }

    public static MaturityLevelDto levelThree() {
        return new MaturityLevelDto(LEVEL_THREE_ID, 3, 3,
            List.of(createCompetence(75, LEVEL_TWO_ID),
                createCompetence(60, LEVEL_THREE_ID)));
    }

    public static MaturityLevelDto levelFour() {
        return new MaturityLevelDto(LEVEL_FOUR_ID, 4, 4,
            List.of(createCompetence(85, LEVEL_TWO_ID),
                createCompetence(75, LEVEL_THREE_ID),
                createCompetence(60, LEVEL_FOUR_ID)));
    }

    public static MaturityLevelDto levelFive() {
        return new MaturityLevelDto(LEVEL_FIVE_ID, 5, 5,
            List.of(createCompetence(95, LEVEL_TWO_ID),
                createCompetence(85, LEVEL_THREE_ID),
                createCompetence(70, LEVEL_FOUR_ID),
                createCompetence(60, LEVEL_FIVE_ID)));
    }

}
