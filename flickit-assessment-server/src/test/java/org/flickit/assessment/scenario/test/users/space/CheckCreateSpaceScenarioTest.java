package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckCreateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    AppSpecProperties appSpecProperties;

    @Test
    void checkCreateSpace_noSpaceCreatedYet() {
        var response = spaceHelper.checkCreate(context);
        response.then()
            .statusCode(200)
            .body("allowCreateBasic", notNullValue());

        boolean allowCreateSpace = response.path("allowCreateBasic");
        assertTrue(allowCreateSpace);
    }

    @Test
    void checkCreateSpace_basicSpacesReachedLimit() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaces();

        // #basic = limit
        createBasicSpaces(limit);

        var response = spaceHelper.checkCreate(context);
        response.then()
            .statusCode(200)
            .body("allowCreateBasic", notNullValue());

        boolean allowCreateSpace = response.path("allowCreateBasic");
        assertFalse(allowCreateSpace);
    }

    @Test
    void checkCreateSpace_basicSpacesReachedLimit_oneDeleted() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaces();

        // #basic = limit
        long lastSpaceId = createBasicSpaces(limit);

        // #basic = limit - 1
        spaceHelper.delete(context, lastSpaceId);

        var response = spaceHelper.checkCreate(context);
        response.then()
            .statusCode(200)
            .body("allowCreateBasic", notNullValue());

        boolean allowCreateSpace = response.path("allowCreateBasic");
        assertTrue(allowCreateSpace);
    }

    @Test
    void checkCreateSpace_basicSpacesAndPremiumSpace() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaces();

        // #basic = limit - 1
        createBasicSpaces(limit - 1);

        // #basic = limit - 1, #premium = 1
        createPremiumSpace();

        var response = spaceHelper.checkCreate(context);
        response.then()
            .statusCode(200)
            .body("allowCreateBasic", notNullValue());

        boolean allowCreateSpace = response.path("allowCreateBasic");
        assertTrue(allowCreateSpace);
    }

    private long createBasicSpaces(int limit) {
        long lastSpaceId = 0;
        for (int i = 0; i < limit; i++)
            lastSpaceId = createBasicSpace();
        return lastSpaceId;
    }

    private long createBasicSpace() {
        var request = createSpaceRequestDto();
        var response = spaceHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private void createPremiumSpace() {
        var request = createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode()));
        spaceHelper.create(context, request);
    }
}
