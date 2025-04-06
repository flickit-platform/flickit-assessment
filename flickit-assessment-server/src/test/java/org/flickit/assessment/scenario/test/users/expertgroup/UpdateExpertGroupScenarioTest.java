package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
