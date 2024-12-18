package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteExpertGroupScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void deleteExpertGroup_whenCurrenUserIsExpertGroupOwner_thenDeleteExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = response.path("id");

        var deleteResponse = expertGroupHelper.delete(context, expertGroupId.longValue());
        deleteResponse.then()
            .statusCode(204);

        ExpertGroupJpaEntity expertGroupEntity = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertTrue(expertGroupEntity.isDeleted());
    }

    @Test
    void deleteExpertGroup_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = response.path("id");
        context.getNextCurrentUser();

        var deleteResponse = expertGroupHelper.delete(context, expertGroupId.longValue());
        deleteResponse.then()
            .statusCode(403);
    }
}
