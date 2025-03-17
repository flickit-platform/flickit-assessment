package org.flickit.assessment.scenario.test.kit.assessmentkit;

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

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.GrantUserAccessToKitRequestDtoMother.grantUserAccessToKitRequestDto;

public class GrantUserAccessToKitScenarioTest extends AbstractScenarioTest {

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
        UUID userId = UUID.randomUUID();
        userHelper.create(CreateUserRequestDtoMother.createUserRequestDto(b -> b.id(userId)));

        var request = grantUserAccessToKitRequestDto(b -> b.userId(userId));

        kitHelper.grantUserAccessToKit(context, request, kitId)
            .then().statusCode(200);
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
