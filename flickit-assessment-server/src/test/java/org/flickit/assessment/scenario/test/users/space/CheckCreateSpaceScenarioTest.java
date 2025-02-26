package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class CheckCreateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void checkCreateSpace_withBasicSpaceLimitIsReached() {
        assertTrue(checkAllowCreateBasic());
        // First basic space creation
        var request = createSpaceRequestDto();
        var firstCreateResponse = spaceHelper.create(context, request);
        firstCreateResponse.then()
            .statusCode(201);

        assertFalse(checkAllowCreateBasic());

        // Delete the space
        spaceHelper.delete(context, firstCreateResponse.body().path("id").toString());

        assertTrue(checkAllowCreateBasic());

        // Second basic space creation;
        spaceHelper.create(context, request)
            .then()
            .statusCode(201);

        assertFalse(checkAllowCreateBasic());
    }

    private boolean checkAllowCreateBasic() {
        return Boolean.TRUE.equals(spaceHelper.checkCreateSpace(context).body().path("allowCreateBasic"));
    }
}
