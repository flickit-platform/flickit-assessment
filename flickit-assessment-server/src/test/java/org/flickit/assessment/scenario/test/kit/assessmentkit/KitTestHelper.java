package org.flickit.assessment.scenario.test.kit.assessmentkit;

import io.restassured.response.Response;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.CreateKitByDslRequestDto;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class KitTestHelper {

    public Response create(ScenarioContext context, CreateKitByDslRequestDto request) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .post("/assessment-core/api/assessment-kits/create-by-dsl")
            .then()
            .extract()
            .response();
    }
}
