package org.flickit.assessment.scenario.test.kit.kitdsl;

import okhttp3.mockwebserver.MockResponse;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.util.FileUtils.createMultipartFile;
import static org.flickit.assessment.scenario.util.FileUtils.readFileToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class UploadKitDslScenarioTest extends AbstractScenarioTest {

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void uploadKitDsl() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = response.path("id");

        MockMultipartFile file = createMultipartFile("dummy-dsl.zip", "dslFile", "application/zip");

        var json = readFileToString("dsl.json");
        mockDslWebServer.enqueue(new MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json"));

        var dslResponse = kitDslHelper.uploadDsl(context, file, expertGroupId.longValue());
        dslResponse.then()
            .statusCode(200)
            .body("kitDslId", notNullValue());

        final Number kitDslId = dslResponse.path("kitDslId");

        KitDslJpaEntity loadedKitDsl = jpaTemplate.load(kitDslId, KitDslJpaEntity.class);
        assertNotNull(loadedKitDsl.getDslPath());
        assertNotNull(loadedKitDsl.getJsonPath());
        assertNull(loadedKitDsl.getKitId());
        assertNotNull(loadedKitDsl.getCreationTime());
        assertNotNull(loadedKitDsl.getLastModificationTime());
        assertEquals(getCurrentUserId(), loadedKitDsl.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedKitDsl.getLastModifiedBy());
    }
}
