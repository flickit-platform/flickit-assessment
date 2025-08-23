package org.flickit.assessment.scenario.test.kit.assessmentkit;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateKitByDslErrorScenarioTest extends AbstractScenarioTest {

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Test
    void createKitByDslErrorScenario_duplicateTitle() {
        final Long expertGroupId = createExpertGroup();
        final Long kitDslId = uploadDsl(expertGroupId);
        final Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
                .expertGroupId(expertGroupId)
                .kitDslId(kitDslId)
                .tagIds(List.of(kitTagId))
        );

        kitHelper.create(context, request)
                .then()
                .statusCode(201);

        final int countBefore = jpaTemplate.count(AssessmentKitJpaEntity.class);

        // Create another kit with same title
        var error = kitHelper.create(context, request)
                .then()
                .statusCode(400)
                .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentKitJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void createKitByDslErrorScenario_currentUserIsNotOwner() {
        final Long expertGroupId = createExpertGroup();
        final Long kitDslId = uploadDsl(expertGroupId);
        final Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
                .expertGroupId(expertGroupId)
                .kitDslId(kitDslId)
                .tagIds(List.of(kitTagId))
        );

        final int countBefore = jpaTemplate.count(AssessmentKitJpaEntity.class);

        // Change currentUser which is not an owner of the expert group
        context.getNextCurrentUser();
        var error = kitHelper.create(context, request)
                .then()
                .statusCode(403)
                .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentKitJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long uploadDsl(Long expertGroupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", expertGroupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }
}
