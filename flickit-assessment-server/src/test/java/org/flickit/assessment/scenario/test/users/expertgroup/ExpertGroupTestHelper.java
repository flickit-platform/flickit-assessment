package org.flickit.assessment.scenario.test.users.expertgroup;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.flickit.assessment.users.adapter.in.rest.expertgroup.CreateExpertGroupRequestDto;
import org.flickit.assessment.users.adapter.in.rest.expertgroup.CreateExpertGroupRequestDto.Fields;
import org.flickit.assessment.users.adapter.in.rest.expertgroup.UpdateExpertGroupRequestDto;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class ExpertGroupTestHelper {

    @SneakyThrows
    public Response create(ScenarioContext context, CreateExpertGroupRequestDto request) {
        var requestSpec = given()
            .contentType("multipart/form-data")
            .auth().oauth2(context.getCurrentUser().getJwt())
            .multiPart(Fields.title, request.title())
            .multiPart(Fields.bio, request.bio())
            .multiPart(Fields.about, request.about());

        if (request.website() != null)
            requestSpec.multiPart(Fields.website, request.website());
        if (request.picture() != null)
            requestSpec.multiPart(Fields.picture,
                request.picture().getOriginalFilename(),
                request.picture().getInputStream(),
                request.picture().getContentType());

        return requestSpec
            .when()
            .post("/assessment-core/api/expert-groups")
            .then()
            .extract()
            .response();
    }

    @SneakyThrows
    public Response update(ScenarioContext context, UpdateExpertGroupRequestDto request, long id) {
        var requestSpec = given()
            .contentType("multipart/form-data")
            .auth().oauth2(context.getCurrentUser().getJwt())
            .multiPart(Fields.title, request.title())
            .multiPart(Fields.bio, request.bio())
            .multiPart(Fields.about, request.about());

        if (request.website() != null)
            requestSpec.multiPart(Fields.website, request.website());

        return requestSpec
            .when()
            .put("/assessment-core/api/expert-groups/" + id)
            .then()
            .extract()
            .response();
    }

    public Response delete(ScenarioContext context, long id) {
        return given()
            .auth().oauth2(context.getCurrentUser().getJwt())
            .when()
            .delete("/assessment-core/api/expert-groups/" + id)
            .then()
            .extract()
            .response();
    }
}
