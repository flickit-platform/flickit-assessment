package org.flickit.assessment.scenario.test.core.assessment;

import io.restassured.response.Response;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.scenario.test.users.space.SpaceTestHelper;
import org.flickit.assessment.scenario.test.users.spaceuseraccess.SpaceUserAccessTestHelper;
import org.flickit.assessment.users.adapter.in.rest.user.CreateUserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.exception.api.ErrorCodes.*;
import static org.flickit.assessment.scenario.fixture.request.AddSpaceMemberRequestDtoMother.addSpaceMemberRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateAssessmentErrorScenarioTest extends AbstractScenarioTest {

    @Autowired
    AssessmentTestHelper assessmentHelper;

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    SpaceUserAccessTestHelper spaceUserAccessHelper;

    @Autowired
    AppSpecProperties appSpecProperties;

    @Test
    void createAssessment_currentUserIsNotSpaceMember() {
        var spaceId = createSpace();

        // Change currentUser which is not an owner of the expert group
        context.getNextCurrentUser();
        var kitId = createKit(false);
        kitHelper.publishKit(context, kitId);

        final int countBefore = jpaTemplate.count(AssessmentJpaEntity.class);

        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));

        var error = assessmentHelper.create(context, request)
            .then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void createAssessment_currentUserDoesNotHaveAccessToPrivateKit() {
        var spaceId = createSpace();

        // Create a private kit
        var kitId = createKit(true);
        kitHelper.publishKit(context, kitId);

        // Create a user intended for addition to the space
        var createUserRequest = createUserRequestDto();
        var newUserId = createUser(createUserRequest);

        // Add the created user to the space
        var addRequest = addSpaceMemberRequestDto(b -> b.email(createUserRequest.email()));
        var response = spaceUserAccessHelper.create(context, spaceId, addRequest);
        response.then()
            .statusCode(200);

        // Set new user as currentUser which is not a member of the expert group and does not have access to kit
        context.setCurrentUser(newUserId);

        final int countBefore = jpaTemplate.count(AssessmentJpaEntity.class);

        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));

        var error = assessmentHelper.create(context, request)
            .then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void createAssessment_duplicateTitle() {
        var spaceId = createSpace();

        var kitId = createKit(false);
        kitHelper.publishKit(context, kitId);

        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));

        assessmentHelper.create(context, request)
            .then()
            .statusCode(201);

        final int countBefore = jpaTemplate.count(AssessmentJpaEntity.class);

        // Create another assessment with same title
        var error = assessmentHelper.create(context, request)
            .then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void createAssessment_basicAssessmentsReachedLimit() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaceId = createSpace();

        var kitId = createKit(false);
        kitHelper.publishKit(context, kitId);
        // #assessments = limit
        createAssessments(spaceId, kitId, limit);

        final int countBefore = jpaTemplate.count(AssessmentJpaEntity.class);

        // Create another assessment
        var error = createAssessment(spaceId, kitId)
            .then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(UPGRADE_REQUIRED, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(AssessmentJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    private void createAssessments(long spaceId, long kitId, int limit) {
        for (int i = 0; i < limit; i++)
            createAssessment(spaceId, kitId);
    }

    private Response createAssessment(Long spaceId, Long kitId) {
        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));
        return assessmentHelper.create(context, request);
    }

    private Long createSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto());
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createKit(boolean isPrivate) {
        Long expertGroupId = createExpertGroup();
        Long kitDslId = uploadDsl(expertGroupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
            .isPrivate(isPrivate)
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        return kitId.longValue();
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

    private UUID createUser(CreateUserRequestDto requestDto) {
        var response = userHelper.create(requestDto);
        return UUID.fromString(response.path("userId"));
    }
}
