package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.UpdateExpertGroupRequestDtoMother.updateExpertGroupRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class UpdateExpertGroupScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void updateExpertGroup() {
        // Create expert group
        final long expertGroupId = createExpertGroup();

        // Update expert group
        var updateRequest = updateExpertGroupRequestDto();
        var updateResponse = expertGroupHelper.update(context, updateRequest, expertGroupId);
        updateResponse.then()
            .statusCode(200);

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);

        assertEquals(updateRequest.title(), loadedExpertGroup.getTitle());
        assertEquals(updateRequest.bio(), loadedExpertGroup.getBio());
        assertEquals(updateRequest.about(), loadedExpertGroup.getAbout());
        assertEquals(generateSlugCode(updateRequest.title()), loadedExpertGroup.getCode());
        assertEquals(updateRequest.website(), loadedExpertGroup.getWebsite());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getLastModifiedBy());
        assertThat(loadedExpertGroup.getLastModificationTime()).isAfter(loadedExpertGroup.getCreationTime());
    }

    @Test
    void updateExpertGroup_withSameTitleAsDeletedExpertGroup() {
        // Create first expert group
        final long firstExpertGroupId = createExpertGroup();
        var firstExpertGroup = jpaTemplate.load(firstExpertGroupId, ExpertGroupJpaEntity.class);

        // Create second expert group
        final Number secondExpertGroupId = createExpertGroup();

        // Delete the expert group
        expertGroupHelper.delete(context, firstExpertGroupId);

        // Update expert group
        var updateRequest = updateExpertGroupRequestDto(b -> b.title(firstExpertGroup.getTitle()));
        var updateResponse = expertGroupHelper.update(context, updateRequest, secondExpertGroupId.longValue());
        updateResponse.then()
            .statusCode(200);

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(secondExpertGroupId, ExpertGroupJpaEntity.class);

        assertEquals(firstExpertGroup.getTitle(), loadedExpertGroup.getTitle());
        assertEquals(updateRequest.bio(), loadedExpertGroup.getBio());
        assertEquals(updateRequest.about(), loadedExpertGroup.getAbout());
        assertEquals(generateSlugCode(updateRequest.title()), loadedExpertGroup.getCode());
        assertEquals(updateRequest.website(), loadedExpertGroup.getWebsite());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getLastModifiedBy());
        assertThat(loadedExpertGroup.getLastModificationTime()).isAfter(loadedExpertGroup.getCreationTime());
    }

    @Test
    void updateExpertGroup_duplicateTitle() {
        // Create first expert group
        final long firstExpertGroupId = createExpertGroup();
        var firstExpertGroup = jpaTemplate.load(firstExpertGroupId, ExpertGroupJpaEntity.class);

        // Create second expert group
        final long secondExpertGroupId = createExpertGroup();
        var updateRequest = updateExpertGroupRequestDto(b -> b.title(firstExpertGroup.getTitle()));

        // Update expert group with the same title as the deleted expert group
        var updateResponse = expertGroupHelper.update(context, updateRequest, secondExpertGroupId);
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
        // Create expert group
        final long expertGroupId = createExpertGroup();
        var expertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);

        // Change the current user
        context.getNextCurrentUser();

        // Update the expert group
        var updateRequest = updateExpertGroupRequestDto(b -> b.title(expertGroup.getTitle()));
        var updateResponse = expertGroupHelper.update(context, updateRequest, expertGroupId);
        var error = updateResponse.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertFalse(loadedExpertGroup.getLastModificationTime().isAfter(loadedExpertGroup.getCreationTime()));
        assertEquals(loadedExpertGroup.getCreatedBy(), loadedExpertGroup.getLastModifiedBy());
    }

    private long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }
}
