package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class DeleteExpertGroupScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void deleteExpertGroup_whenCurrentUserIsExpertGroupOwner_thenDeleteExpertGroup() {
        var createRequest = createExpertGroupRequestDto();
        var createResponse = expertGroupHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = createResponse.path("id");

        expertGroupHelper.delete(context, expertGroupId.longValue()).then()
            .statusCode(204);

        ExpertGroupJpaEntity deletedExpertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertTrue(deletedExpertGroup.isDeleted());
        assertThat(deletedExpertGroup.getDeletionTime()).isPositive();
    }

    @Test
    void deleteExpertGroup_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        // Create an expert group
        var creteRequest = createExpertGroupRequestDto();
        var creteResponse = expertGroupHelper.create(context, creteRequest);

        creteResponse.then()
            .statusCode(201);

        final Number expertGroupId = creteResponse.path("id");
        // Change currentUser which is not owner (creator) of the expert group
        context.getNextCurrentUser();

        // Delete expert group by non owner user
        var response = expertGroupHelper.delete(context, expertGroupId.longValue());

        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        ExpertGroupJpaEntity expertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertFalse(expertGroup.isDeleted());
        assertEquals(0, expertGroup.getDeletionTime());
    }
}
