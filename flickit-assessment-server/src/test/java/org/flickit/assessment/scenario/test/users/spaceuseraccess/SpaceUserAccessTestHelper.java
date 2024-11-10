package org.flickit.assessment.scenario.test.users.spaceuseraccess;

import io.restassured.response.Response;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.flickit.assessment.users.adapter.in.rest.spaceuseraccess.AddSpaceMemberRequestDto;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class SpaceUserAccessTestHelper {

    public Response create(ScenarioContext context, long spaceId, AddSpaceMemberRequestDto request) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .post("/assessment-core/api/spaces/{id}/members", spaceId)
            .then()
            .extract()
            .response();
    }
}
