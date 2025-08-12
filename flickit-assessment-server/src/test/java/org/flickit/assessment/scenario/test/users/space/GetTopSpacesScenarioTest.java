package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.core.assessment.AssessmentTestHelper;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.users.adapter.in.rest.space.GetTopSpacesResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.UPGRADE_REQUIRED;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class GetTopSpacesScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    AssessmentTestHelper assessmentHelper;

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Autowired
    AppSpecProperties appSpecProperties;

    @Test
    void topSpaces_whenOneBasicSpaceWithCapacityExists_thenReturnIt() {
        // A basic space (default space) created upon user creation
        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());

        var space = items.getFirst();
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertTrue(space.isDefault());
    }

    @Test
    void topSpaces_whenOnlyOneBasicSpaceExistsAndItIsFull_failure() {
        // A basic space (default space) created upon user creation
        var spaceId = loadSpaceByOwnerId(context.getCurrentUser().getUserId()).getFirst().getId();
        createAssessments(spaceId, appSpecProperties.getSpace().getMaxBasicSpaces());

        var response = spaceHelper.getTopSpaces(context)
                .then()
                .statusCode(403)
                .extract().as(ErrorResponseDto.class);

        assertEquals(UPGRADE_REQUIRED, response.code());
        assertNotNull(response.message());
    }

    @Test
    void topSpaces_whenBasicSpaceIsFullAndPremiumSpaceExists_thenOnlyReturnPremiumSpace() {
        // A basic space (default space) created upon user creation
        var defaultSpaceId = loadSpaceByOwnerId(context.getCurrentUser().getUserId()).getFirst().getId();
        createAssessments(defaultSpaceId, appSpecProperties.getSpace().getMaxBasicSpaces());
        var premiumSpaceTitle = "Premium Space Title";
        var premiumSpaceId = createPremiumSpace(premiumSpaceTitle);

        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());

        var space = items.getFirst();
        assertEquals(premiumSpaceId, space.id());
        assertEquals(premiumSpaceTitle, space.title());
        assertEquals(SpaceType.PREMIUM.getCode(), space.type().code());
        assertTrue(space.isDefault());
    }

    @Test
    void topSpaces_whenOnePremiumAndOneBasicSpaceWithCapacityExist_thenReturnPremiumAsDefault() {
        // A basic space (default space) created upon user creation
        var defaultSpaceId = loadSpaceByOwnerId(context.getCurrentUser().getUserId()).getFirst().getId();
        var premiumSpaceTitle = "Premium Space Title";
        var premiumSpaceId = createPremiumSpace(premiumSpaceTitle);

        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(2, items.size());
        var premiumSpace = items.stream().filter(e -> e.type().code().equals(SpaceType.PREMIUM.getCode())).toList().getFirst();
        assertTrue(premiumSpace.isDefault());
        assertEquals(premiumSpaceId, premiumSpace.id());
        assertThat(items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault)).hasSize(1);

        var basicSpace = items.stream().filter(e -> e.type().code().equals(SpaceType.BASIC.getCode())).toList().getFirst();
        assertEquals(defaultSpaceId, basicSpace.id());
        assertFalse(basicSpace.isDefault());
    }

    @Test
    void topSpaces_whenTwoBasicSpacesOneFullOneWithCapacityExist_thenOnlyReturnTheOneWithCapacity() {
        // A basic space (default space) created upon user creation
        var defaultSpace = loadSpaceByOwnerId(context.getCurrentUser().getUserId()).getFirst();
        var basicSpaceTitle2 = "Basic Space 2";
        var basicSpaceId2 = createBasicSpace(basicSpaceTitle2);
        createAssessments(basicSpaceId2, appSpecProperties.getSpace().getMaxBasicSpaceAssessments());

        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());

        var space = items.getFirst();
        assertEquals(defaultSpace.getId(), space.id());
        assertEquals(defaultSpace.getTitle(), space.title());
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertTrue(space.isDefault());
    }

    @Test
    void topSpaces_whenMultipleSpacesWithCapacityExist_thenReturnAllAndOneOfPremiumsAsDefault() {
        // A basic space (default space) created upon user creation
        var basicSpaceTitle = "Basic Space Title";
       createBasicSpace(basicSpaceTitle);
        var premiumSpaceTitle = "Premium Space Title";
        IntStream.range(0, 9).forEach(i -> createPremiumSpace(premiumSpaceTitle + i));

        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        // Total spaces: 12 (1 basic + 9 premium + 1 default (basic)), but limited to 10
        assertEquals(10, items.size());
        var defaultSpace = items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault).toList().getFirst();
        assertTrue(defaultSpace.isDefault());
        assertEquals(SpaceType.PREMIUM.getCode(), defaultSpace.type().code());
        assertThat(items.stream().filter(e -> e.type().code().equals(SpaceType.BASIC.getCode()))).hasSize(1);
        assertThat(items.stream().filter(e -> e.type().code().equals(SpaceType.PREMIUM.getCode()))).hasSize(9);
        assertThat(items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault)).hasSize(1);
    }

    @Test
    void topSpaces_whenMultipleBasicSpacesExist_thenReturnAllAndOneOfThemAsDefault() {
        // A basic space (default space) created upon user creation
        var basicSpaceTitle = "Basic Space Title";
        IntStream.range(0, appSpecProperties.getSpace().getMaxBasicSpaces() - 1).forEach(i -> createBasicSpace(basicSpaceTitle + i));

        var response = getTopSpacesResponse();

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        // One for the default Space
        assertEquals(appSpecProperties.getSpace().getMaxBasicSpaces(), items.size());

        var defaultSpace = items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault).toList().getFirst();
        assertEquals(SpaceType.BASIC.getCode(), defaultSpace.type().code());
        assertThat(items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault)).hasSize(1);
    }

    private Long createBasicSpace(String title) {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.title(title)));
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createPremiumSpace(String title) {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.title(title).type(SpaceType.PREMIUM.getCode())));
        Number id = response.path("id");
        return id.longValue();
    }

    private GetTopSpacesResponseDto getTopSpacesResponse() {
        return spaceHelper.getTopSpaces(context)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(GetTopSpacesResponseDto.class);
    }

    private void createAssessments(long spaceId, int limit) {
        var kitId = createKit();
        kitHelper.publishKit(context, kitId);

        for (int i = 0; i < limit; i++)
            createAssessment(spaceId, kitId);
    }

    private void createAssessment(Long spaceId, Long kitId) {
        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
                .spaceId(spaceId)
                .assessmentKitId(kitId));
        assessmentHelper.create(context, request);
    }

    private Long createKit() {
        Long expertGroupId = createExpertGroup();
        Long kitDslId = uploadDsl(expertGroupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
                .expertGroupId(expertGroupId)
                .kitDslId(kitDslId)
                .tagIds(List.of(kitTagId))
                .isPrivate(false)
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        kitHelper.publishKit(context, kitId.longValue());
        return kitId.longValue();
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long uploadDsl(Long expertGroupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", expertGroupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }

    private List<SpaceJpaEntity> loadSpaceByOwnerId(UUID ownerId) {
        return jpaTemplate.search(SpaceJpaEntity.class,
                (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId));
    }
}
