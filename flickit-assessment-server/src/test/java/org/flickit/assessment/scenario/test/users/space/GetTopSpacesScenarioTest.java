package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.MessageBundle;
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
import java.util.Locale;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.UPGRADE_REQUIRED;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.users.common.MessageKey.SPACE_DRAFT_TITLE;
import static org.junit.jupiter.api.Assertions.*;

public class GetTopSpacesScenarioTest extends AbstractScenarioTest {

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
    void topSpaces_whenNoSpaceExistedLangIsEN() {
        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = spaceHelper.getTopSpaces(context, Locale.ENGLISH.toString())
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(GetTopSpacesResponseDto.class);

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(MessageBundle.message(SPACE_DRAFT_TITLE, Locale.ENGLISH), space.title());
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), space.type().title());
        assertTrue(space.isDefault());
        assertEquals(countBefore + 1, countAfter);
    }

    @Test
    void topSpaces_whenNoSpaceExistedLangIsFA() {
        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = spaceHelper.getTopSpaces(context, "FA")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(GetTopSpacesResponseDto.class);

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(MessageBundle.message(SPACE_DRAFT_TITLE, Locale.of("FA")), space.title());
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertTrue(space.isDefault());
        assertEquals(countBefore + 1, countAfter);
    }

    @Test
    void topSpaces_whenOneBasicSpaceWithCapacityExists() {
        var spaceTitle = "Space Title";
        var spaceId = createBasicSpace(spaceTitle);
        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(spaceId, space.id());
        assertEquals(spaceTitle, space.title());
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertTrue(space.isDefault());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenOnlyOneBasicSpaceExistsAndIsFull() {
        var spaceTitle = "Space Title";
        var spaceId = createBasicSpace(spaceTitle);
        createAssessments(spaceId, appSpecProperties.getSpace().getMaxBasicSpaces());

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = spaceHelper.getTopSpaces(context)
            .then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertEquals(UPGRADE_REQUIRED, response.code());
        assertNotNull(response.message());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenOnlyOnePremiumSpaceExists() {
        var spaceTitle = "Premium Space Title";
        var spaceId = createPremiumSpace(spaceTitle);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(spaceId, space.id());
        assertEquals(spaceTitle, space.title());
        assertEquals(SpaceType.PREMIUM.getCode(), space.type().code());
        assertTrue(space.isDefault());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenBasicSpaceIsFullAndPremiumSpaceExists() {
        var basicSpaceTitle = "Basic Space Title";
        var basicSpaceId = createBasicSpace(basicSpaceTitle);
        createAssessments(basicSpaceId, appSpecProperties.getSpace().getMaxBasicSpaces());
        var premiumSpaceTitle = "Premium Space Title";
        var premiumSpaceId = createPremiumSpace(premiumSpaceTitle);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(premiumSpaceId, space.id());
        assertEquals(premiumSpaceTitle, space.title());
        assertEquals(SpaceType.PREMIUM.getCode(), space.type().code());
        assertTrue(space.isDefault());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenOnePremiumAndOneBasicSpaceWithCapacityExist() {
        var basicSpaceTitle = "Basic Space Title";
        var basicSpaceId = createBasicSpace(basicSpaceTitle);
        var premiumSpaceTitle = "Premium Space Title";
        var premiumSpaceId = createPremiumSpace(premiumSpaceTitle);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(2, items.size());
        var premiumSpace = items.stream().filter(e -> e.type().code().equals(SpaceType.PREMIUM.getCode())).toList().getFirst();
        assertTrue(premiumSpace.isDefault());
        assertEquals(premiumSpaceId, premiumSpace.id());
        var basicSpace = items.stream().filter(e -> e.type().code().equals(SpaceType.BASIC.getCode())).toList().getFirst();
        assertEquals(basicSpaceId, basicSpace.id());
        assertFalse(basicSpace.isDefault());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenTwoBasicSpacesOneFullOneWithCapacityExist() {
        var basicSpaceTitle1 = "Basic Space 1";
        var basicSpaceId1 = createBasicSpace(basicSpaceTitle1);
        var basicSpaceTitle2 = "Basic Space 2";
        var basicSpaceId2 = createBasicSpace(basicSpaceTitle2);
        createAssessments(basicSpaceId2, appSpecProperties.getSpace().getMaxBasicSpaceAssessments());

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        var space = items.getFirst();
        assertEquals(basicSpaceId1, space.id());
        assertEquals(basicSpaceTitle1, space.title());
        assertEquals(SpaceType.BASIC.getCode(), space.type().code());
        assertTrue(space.isDefault());
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenMultipleSpacesWithCapacityExist() {
        var basicSpaceTitle = "Basic Space Title";
        IntStream.range(0, 2).forEach(i -> createBasicSpace(basicSpaceTitle + i));
        var premiumSpaceTitle = "Premium Space Title";
        IntStream.range(0, 9).forEach(i -> createPremiumSpace(premiumSpaceTitle + i));

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        var defaultSpace = items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault).toList().getFirst();
        assertTrue(defaultSpace.isDefault());
        assertEquals(SpaceType.PREMIUM.getCode(), defaultSpace.type().code());
        assertThat(items.stream().filter(e -> e.type().code().equals(SpaceType.BASIC.getCode()))).hasSize(1);
        assertThat(items.stream().filter(e -> e.type().code().equals(SpaceType.PREMIUM.getCode()))).hasSize(9);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenMultiplePremiumSpacesExist() {
        var premiumSpaceTitle = "Premium Space Title";
        IntStream.range(0, 3).forEach(i -> createPremiumSpace(premiumSpaceTitle + i));

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
        var defaultSpace = items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault).toList().getFirst();
        assertTrue(defaultSpace.isDefault());
        assertEquals(SpaceType.PREMIUM.getCode(), defaultSpace.type().code());
        assertThat(items.stream().filter(e -> e.type().code().equals(SpaceType.PREMIUM.getCode()))).hasSize(3);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void topSpaces_whenMultipleBasicSpacesExist() {
        var basicSpaceTitle = "Basic Space Title";
        IntStream.range(0, appSpecProperties.getSpace().getMaxBasicSpaceAssessments()).forEach(i -> createBasicSpace(basicSpaceTitle + i));

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        var response = getTopSpacesResponse();

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertNotNull(response);
        var items = response.items();
        assertFalse(items.isEmpty());
        assertEquals(appSpecProperties.getSpace().getMaxBasicSpaceAssessments(), items.size());
        var defaultSpace = items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault).toList().getFirst();
        assertEquals(SpaceType.BASIC.getCode(), defaultSpace.type().code());
        assertThat(items.stream().filter(GetTopSpacesResponseDto.SpaceListItemDto::isDefault)).hasSize(1);
        assertEquals(countBefore, countAfter);
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
}
