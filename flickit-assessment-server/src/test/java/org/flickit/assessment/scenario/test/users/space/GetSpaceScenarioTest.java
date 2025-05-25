package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.core.assessment.AssessmentTestHelper;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.scenario.test.users.spaceuseraccess.SpaceUserAccessTestHelper;
import org.flickit.assessment.users.adapter.in.rest.space.GetSpaceResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.AddSpaceMemberRequestDtoMother.addSpaceMemberRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class GetSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

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
    SpaceUserAccessTestHelper spaceUserAccessHelper;

    @Autowired
    AppSpecProperties appSpecProperties;

    @Test
    void getSpace() {
        var title = "new space";
        var spaceId = createBasicSpace(title);

        // Create assessments and delete one of them
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var lastAssessmentId = createAssessments(spaceId, limit);
        deleteAssessment(lastAssessmentId);

        // Add another member to space
        addMember(spaceId);

        var result = spaceHelper.get(context, spaceId).then()
            .statusCode(200)
            .extract()
            .body()
            .as(GetSpaceResponseDto.class);

        // Assert Space
        assertEquals(spaceId, result.id());
        assertEquals(title, result.title());
        assertEquals(generateSlugCode(title), result.code());
        assertTrue(result.editable());
        assertNotNull(result.lastModificationTime());
        assertEquals(limit - 1, result.assessmentsCount());
        assertEquals(2, result.membersCount());
        assertTrue(result.canCreateAssessment());
        // Assert SpaceType
        assertEquals(SpaceType.BASIC.getCode(), result.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), result.type().title());
    }

    @Test
    void getSpace_withBasicSpace_assessmentLimitReached_byNonOwer() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaceId = createBasicSpace("new space");

        var newMemberId = addMember(spaceId);

        // Set new user as currentUser which is not owner of the space and does not have access to edit it
        context.setCurrentUser(newMemberId);

        // #assessments = limit
        createAssessments(spaceId, limit);
        var result = spaceHelper.get(context, spaceId).then()
            .statusCode(200)
            .extract()
            .body()
            .as(GetSpaceResponseDto.class);

        assertFalse(result.editable());

        // If the limit has been reached, no additional assessments can be created.
        assertEquals(limit, result.assessmentsCount());
        assertFalse(result.canCreateAssessment());
    }

    @Test
    void getSpace_withPremiumSpace_basicSpaceAssessmentLimitReached() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaceId = createPremiumSpace();

        // #assessments = limit
        createAssessments(spaceId, limit);
        // Check the `canCreateAssessment` field of the space.
        var result = spaceHelper.get(context, spaceId).then()
            .statusCode(200)
            .extract()
            .body()
            .as(GetSpaceResponseDto.class);

        // If the limit for the basic space has been reached, an assessment can still be created in a premium space.
        assertEquals(limit, result.assessmentsCount());
        assertTrue(result.canCreateAssessment());
    }

    @Test
    void getSpace_notAllowed() {
        // Create a space
        Number spaceId = createBasicSpace("space");
        // Change currentUser which is not a member of the space
        context.getNextCurrentUser();

        // Get space by not member user
        var response = spaceHelper.get(context, spaceId);

        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());
    }

    private Long createBasicSpace(String title) {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.title(title)));
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createPremiumSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode())));
        Number id = response.path("id");
        return id.longValue();
    }

    private UUID createAssessments(long spaceId, int limit) {
        var kitId = createKit();
        kitHelper.publishKit(context, kitId);

        UUID lastAssessmentId = null;
        for (int i = 0; i < limit; i++)
            lastAssessmentId = createAssessment(spaceId, kitId);
        return lastAssessmentId;
    }

    private UUID createAssessment(Long spaceId, Long kitId) {
        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));
        var response = assessmentHelper.create(context, request);

        return UUID.fromString(response.path("id"));
    }

    private UUID addMember(long spaceId) {
        var createUserRequest = createUserRequestDto();
        var createUserResponse = userHelper.create(createUserRequest);
        createUserResponse.then()
            .statusCode(201);

        var request = addSpaceMemberRequestDto(b -> b.email(createUserRequest.email()));
        spaceUserAccessHelper.create(context, spaceId, request);

        return UUID.fromString(createUserResponse.path("userId"));
    }

    private void deleteAssessment(UUID assessmentId) {
        assessmentHelper.delete(context, assessmentId.toString())
            .then()
            .statusCode(204);
    }

    private Long createKit() {
        Long expertGroupId = createExpertGroup();
        Long kitDslId = uploadDsl(expertGroupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
            .isPrivate(false)
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        kitHelper.publishKit(context, kitId.longValue());
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
}
