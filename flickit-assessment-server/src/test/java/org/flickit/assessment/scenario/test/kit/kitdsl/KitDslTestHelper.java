package org.flickit.assessment.scenario.test.kit.kitdsl;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static io.restassured.RestAssured.given;

@Component
public class KitDslTestHelper {

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
