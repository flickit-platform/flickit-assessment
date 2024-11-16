package org.flickit.assessment.scenario.test.users.user;

import io.restassured.response.Response;
import org.flickit.assessment.users.adapter.in.rest.user.CreateUserRequestDto;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class UserTestHelper {

    public Response create(CreateUserRequestDto request) {
        return given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/assessment-core/api/users")
            .then()
            .extract()
            .response();
    }
}
