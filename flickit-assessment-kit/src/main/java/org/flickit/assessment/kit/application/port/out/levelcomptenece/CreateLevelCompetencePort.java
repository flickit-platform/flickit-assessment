package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface CreateLevelCompetencePort {

    Long persist(String levelCompetenceTitle, Integer value, String maturityLevelTitle);
}
