package org.flickit.assessment.scenario.test.kit.kitdsl;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static io.restassured.RestAssured.given;
import static org.flickit.assessment.scenario.util.FileUtils.createMultipartFile;
import static org.flickit.assessment.scenario.util.FileUtils.readFileToString;

@Component
public class KitDslTestHelper {

    public Response uploadDsl(ScenarioContext context, String zipFile, String jsonFile, Long expertGroupId) {
        MockMultipartFile file = createMultipartFile(zipFile, "dslFile", "application/zip");

        var json = readFileToString(jsonFile);
        context.getMockDslWebServer().enqueue(new MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json"));

        return uploadDsl(context, file, expertGroupId);
    }

    @SneakyThrows
    public Response uploadDsl(ScenarioContext context, MultipartFile dslFile, Long expertGroupId) {
        var requestSpec = given()
            .contentType("multipart/form-data")
            .auth().oauth2(context.getCurrentUser().getJwt())
            .multiPart("dslFile",
                dslFile.getOriginalFilename(),
                dslFile.getInputStream(),
                dslFile.getContentType())
            .multiPart("expertGroupId", expertGroupId);

        return requestSpec
            .when()
            .post("/assessment-core/api/assessment-kits/upload-dsl")
            .then()
            .extract()
            .response();
    }
}
