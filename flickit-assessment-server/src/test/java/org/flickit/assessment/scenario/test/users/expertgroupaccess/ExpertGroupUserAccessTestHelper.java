package org.flickit.assessment.scenario.test.users.expertgroupaccess;

import io.restassured.response.Response;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.flickit.assessment.users.adapter.in.rest.expertgroupaccess.InviteExpertGroupMemberRequestDto;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class ExpertGroupUserAccessTestHelper {

    public Response invite(ScenarioContext context, long expertGroupId, InviteExpertGroupMemberRequestDto request) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .post("/assessment-core/api/expert-groups/{id}/invite", expertGroupId)
            .then()
            .extract()
            .response();
    }

    public Response confirm(ScenarioContext context, long expertGroupId, String inviteToken) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .when()
            .put("/assessment-core/api/expert-groups/{id}/invite/{inviteToken}/confirm", expertGroupId, inviteToken)
            .then()
            .extract()
            .response();
    }

    public Response updateLastSeen(ScenarioContext context, long expertGroupId) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .when()
            .put("/assessment-core/api/expert-groups/{id}/seen", expertGroupId)
            .then()
            .extract()
            .response();
    }
}
