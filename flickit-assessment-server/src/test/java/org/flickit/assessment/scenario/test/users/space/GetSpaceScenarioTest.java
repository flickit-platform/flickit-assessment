package org.flickit.assessment.scenario.test.users.space;

import io.restassured.response.Response;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.core.assessment.AssessmentTestHelper;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class GetSpaceScenarioTest extends AbstractScenarioTest {

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
    AppSpecProperties appSpecProperties;


    @Test
    void getSpace() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        spaceHelper.get(context, spaceId).then()
            .statusCode(200);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertNotNull(space);
    }

    @Test
    void getSpace_withBasicSpace_assessmentLimitReached() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaceId = createBasicSpace();

        var kitId = createKit();
        kitHelper.publishKit(context, kitId);
        // #assessments = limit
        createAssessments(spaceId, kitId, limit);
        // Check the `canCreateAssessment` field of the space.
        Response getResponse = spaceHelper.get(context, spaceId);
        getResponse.then()
            .statusCode(200)
            .body("canCreateAssessment", notNullValue());

        Boolean canCreateAssessment = getResponse.body().path("canCreateAssessment");
        // If the limit has been reached, no additional assessments can be created.
        assertFalse(canCreateAssessment);
    }

    @Test
    void getSpace_withPremiumSpace_basicSpaceAssessmentLimitReached() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaceId = createPremiumSpace();

        var kitId = createKit();
        kitHelper.publishKit(context, kitId);
        // #assessments = limit
        createAssessments(spaceId, kitId, limit);
        // Check the `canCreateAssessment` field of the space.
        Response getResponse = spaceHelper.get(context, spaceId);
        getResponse.then()
            .statusCode(200)
            .body("canCreateAssessment", notNullValue());

        Boolean canCreateAssessment = getResponse.body().path("canCreateAssessment");
        // If the limit for the basic space has been reached, an assessment can still be created in a premium space.
        assertTrue(canCreateAssessment);
    }

    private void createAssessments(long spaceId, long kitId, int limit) {
        for (int i = 0; i < limit; i++)
            createAssessment(spaceId, kitId);
    }

    private void createAssessment(Long spaceId, Long kitId) {
        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));
        assessmentHelper.create(context, request);
    }

    private Long createBasicSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto());
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createPremiumSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode())));
        Number id = response.path("id");
        return id.longValue();
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
