package org.flickit.assessment.scenario.test.users.space;

import io.restassured.response.Response;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.flickit.assessment.users.adapter.in.rest.space.CreateSpaceRequestDto;
import org.flickit.assessment.users.adapter.in.rest.space.UpdateSpaceRequestDto;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class SpaceTestHelper {

    public Response create(ScenarioContext context, CreateSpaceRequestDto request) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .post("/assessment-core/api/spaces")
            .then()
            .extract()
            .response();
    }

    public Response delete(ScenarioContext context, Number spaceId) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .when()
            .delete("/assessment-core/api/spaces/" + spaceId)
            .then()
            .extract()
            .response();
    }

    public Response update(ScenarioContext context, UpdateSpaceRequestDto request, Number spaceId) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .put("/assessment-core/api/spaces/" + spaceId)
            .then()
            .extract()
            .response();
    }

    public Response checkCreate(ScenarioContext context) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .when()
            .get("/assessment-core/api/check-create-space")
            .then()
            .extract()
            .response();
    }
}
