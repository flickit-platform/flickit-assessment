package org.flickit.assessment.scenario.test.kit.kitdsl;

import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.ServerException;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import static org.flickit.assessment.common.error.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
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

    @Autowired
    MinioClient minioClient;

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
        assertDoesNotThrow(() -> checkFileExistenceInMinio(loadedKitDsl.getDslPath()));
        assertDoesNotThrow(() -> checkFileExistenceInMinio(loadedKitDsl.getJsonPath()));
    }

    @Test
    void uploadKitDsl_notAllowed() {
        // Create an expert group
        var createRequest = createExpertGroupRequestDto();
        var createResponse = expertGroupHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = createResponse.path("id");

        MockMultipartFile file = createMultipartFile("dummy-dsl.zip", "dslFile", "application/zip");

        var json = readFileToString("dsl.json");
        mockDslWebServer.enqueue(new MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json"));

        // Change currentUser which is not owner (creator) of the expert group
        context.getNextCurrentUser();

        final int countBefore = jpaTemplate.count(KitDslJpaEntity.class);

        // Upload dsl by non owner user
        var response = kitDslHelper.uploadDsl(context, file, expertGroupId.longValue());
        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(KitDslJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @SneakyThrows
    private void checkFileExistenceInMinio(String filePath) {
        String bucketName = filePath.substring(0, filePath.indexOf("/"));
        String objectName = filePath.substring(filePath.indexOf("/") + 1);

        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(FILE_STORAGE_FILE_NOT_FOUND);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }
    }
}
