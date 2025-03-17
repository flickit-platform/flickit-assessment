package org.flickit.assessment.scenario.test.kit.assessmentkit;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.scenario.test.users.user.UserTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.GrantUserAccessToKitRequestDtoMother.grantUserAccessToKitRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrantUserAccessToKitScenarioTest extends AbstractScenarioTest {

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Autowired
    UserTestHelper userHelper;

    @Test
    void grantUserAccessToKit() {
        var expertGroupId = createExpertGroup();
        int kitId = createKit(expertGroupId);
        // Generate a new user ID and create the user
        UUID userId = UUID.randomUUID();
        userHelper.create(CreateUserRequestDtoMother.createUserRequestDto(b -> b.id(userId)));

        var request = grantUserAccessToKitRequestDto(b -> b.userId(userId));

        kitHelper.grantUserAccessToKit(context, request, kitId)
            .then().statusCode(200);
    }

    @Test
    void grantUserAccessToKit_currentUserIsNotExpertGroupOwner() {
        var expertGroupId = createExpertGroup();
        int kitId = createKit(expertGroupId);
        // Generate a new user ID and create the user
        UUID userId = UUID.randomUUID();
        userHelper.create(CreateUserRequestDtoMother.createUserRequestDto(b -> b.id(userId)));
        //Change the current user
        context.getNextCurrentUser();
        var request = grantUserAccessToKitRequestDto(b -> b.userId(userId));
        var error = kitHelper.grantUserAccessToKit(context, request, kitId)
            .then().statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());
    }

    Integer createKit(long expertGroupId) {
        final Long kitDslId = uploadDsl(expertGroupId);
        final Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
            .isPrivate(true)
        );

        var response = kitHelper.create(context, request);
        return response.path("kitId");
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
