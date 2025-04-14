package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.UpdateExpertGroupRequestDtoMother.updateExpertGroupRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class UpdateExpertGroupScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void updateExpertGroup() {
        var request = createExpertGroupRequestDto();
        // Create expert group
        var response = expertGroupHelper.create(context, request);
        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = response.path("id");

        var updateRequest = updateExpertGroupRequestDto();
        // Update expert group
        var updateResponse = expertGroupHelper.update(context, updateRequest, expertGroupId.longValue());
        updateResponse.then()
            .statusCode(200);

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertEquals(updateRequest.title(), loadedExpertGroup.getTitle());
        assertEquals(updateRequest.bio(), loadedExpertGroup.getBio());
        assertEquals(updateRequest.about(), loadedExpertGroup.getAbout());
        assertEquals(generateSlugCode(updateRequest.title()), loadedExpertGroup.getCode());
        assertEquals(updateRequest.website(), loadedExpertGroup.getWebsite());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getLastModifiedBy());
        assertTrue(loadedExpertGroup.getLastModificationTime().isAfter(loadedExpertGroup.getCreationTime()));
    }

    @Test
    void updateExpertGroup_withSameTitleAsDeletedExpertGroup() {
        var firstRequest = createExpertGroupRequestDto();
        // Create expert group
        var firstResponse = expertGroupHelper.create(context, firstRequest);
        firstResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number firstExpertGroupId = firstResponse.path("id");
        // Delete the expert group
        expertGroupHelper.delete(context, firstExpertGroupId.longValue());

        var secondRequest = createExpertGroupRequestDto();
        // Create new expert group
        var secondResponse = expertGroupHelper.create(context, secondRequest);
        secondResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number secondExpertGroupId = secondResponse.path("id");

        var updateRequest = updateExpertGroupRequestDto(b -> b.title(firstRequest.title()));
        // Update expert group
        var updateResponse = expertGroupHelper.update(context, updateRequest, secondExpertGroupId.longValue());
        updateResponse.then()
            .statusCode(200);

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(secondExpertGroupId, ExpertGroupJpaEntity.class);
        assertEquals(firstRequest.title(), loadedExpertGroup.getTitle());
        assertEquals(updateRequest.bio(), loadedExpertGroup.getBio());
        assertEquals(updateRequest.about(), loadedExpertGroup.getAbout());
        assertEquals(generateSlugCode(updateRequest.title()), loadedExpertGroup.getCode());
        assertEquals(updateRequest.website(), loadedExpertGroup.getWebsite());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getLastModifiedBy());
        assertTrue(loadedExpertGroup.getLastModificationTime().isAfter(loadedExpertGroup.getCreationTime()));
    }

    @Test
    void updateExpertGroup_duplicateTitle() {
        var firstRequest = createExpertGroupRequestDto();
        // Create first expert group
        var firstResponse = expertGroupHelper.create(context, firstRequest);
        firstResponse.then()
            .statusCode(201);

        var secondRequest = createExpertGroupRequestDto();
        // Create second expert group
        var secondResponse = expertGroupHelper.create(context, secondRequest);
        secondResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number secondExpertGroupId = secondResponse.path("id");
        var updateRequest = updateExpertGroupRequestDto(b -> b.title(firstRequest.title()));
        // Update expert group with the same title as the deleted expert group
        var updateResponse = expertGroupHelper.update(context, updateRequest, secondExpertGroupId.longValue());
        var error = updateResponse.then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(secondExpertGroupId, ExpertGroupJpaEntity.class);
        assertEquals(loadedExpertGroup.getLastModificationTime(), loadedExpertGroup.getCreationTime());
    }

    @Test
    void updateExpertGroup_userIsNotOwner() {
        var createRequest = createExpertGroupRequestDto();
        // Create expert group
        var firstResponse = expertGroupHelper.create(context, createRequest);
        firstResponse.then()
            .statusCode(201);

        final Number secondExpertGroupId = firstResponse.path("id");
        // Change the user
        context.getNextCurrentUser();
        var updateRequest = updateExpertGroupRequestDto(b -> b.title(createRequest.title()));
        // Update the expert group
        var updateResponse = expertGroupHelper.update(context, updateRequest, secondExpertGroupId.longValue());
        var error = updateResponse.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(secondExpertGroupId, ExpertGroupJpaEntity.class);
        assertFalse(loadedExpertGroup.getLastModificationTime().isAfter(loadedExpertGroup.getCreationTime()));
        assertEquals(loadedExpertGroup.getCreatedBy(), loadedExpertGroup.getLastModifiedBy());
    }
}
