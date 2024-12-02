package org.flickit.assessment.kit.application.service.kitcustom;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitCustomSubjectServiceTest {

    @InjectMocks
    private GetKitCustomSubjectService service;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private LoadKitCustomPort loadKitCustomPort;

    @Test
    void testGetKitCustomSubject_WhenKitIsPrivateAndCurrentUserHasNoAccessToKit_ThenThrowAccessDeniedException() {
        var param = createParam(GetKitCustomSubjectUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.getKitCustomSubject(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());

        verifyNoInteractions(loadSubjectsPort, loadKitCustomPort);
    }

    @Test
    void testGetKitCustomSubject_WhenKitIsPrivateAndCurrentUserHasAccessToKitAndAllSubjectAndAttributesCustomized_ThenGetKitCustomSubject() {
        var param = createParam(GetKitCustomSubjectUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();

        Attribute attribute = AttributeMother.attributeWithTitle("flexibility");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var subjectCustom = new KitCustomData.Subject(subject.getId(), 2);
        var attributeCustom = new KitCustomData.Attribute(attribute.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(List.of(subjectCustom), List.of(attributeCustom));
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", kit.getId(), kitCustomData);

        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(List.of(subject),
            1,
            1,
            "asc",
            "index",
            1);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(loadKitCustomPort.loadByIdAndKitId(param.getKitCustomId(), param.getKitId())).thenReturn(kitCustom);
        when(loadSubjectsPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomSubjectUseCase.Subject> resultPaginatedResponse = service.getKitCustomSubject(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomSubjectUseCase.Subject> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedItem = paginatedResponse.getItems().getFirst();
        var actualItem = actualItems.getFirst();
        assertNotNull(actualItem);
        assertEquals(expectedItem.getId(), actualItem.id());
        assertEquals(expectedItem.getTitle(), actualItem.title());
        assertEquals(expectedItem.getIndex(), actualItem.index());
        assertEquals(subjectCustom.weight(), actualItem.weight().customValue());
        assertEquals(expectedItem.getWeight(), actualItem.weight().defaultValue());
        assertEquals(expectedItem.getAttributes().size(), actualItem.attributes().size());

        var expectedAttribute = expectedItem.getAttributes().getFirst();
        var actualAttribute = actualItem.attributes().getFirst();
        assertNotNull(actualAttribute);
        assertEquals(expectedAttribute.getId(), actualAttribute.id());
        assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
        assertEquals(expectedAttribute.getIndex(), actualAttribute.index());
        assertEquals(attributeCustom.weight(), actualAttribute.weight().customValue());
        assertEquals(expectedAttribute.getWeight(), actualAttribute.weight().defaultValue());
    }

    @Test
    void testGetKitCustomSubject_WhenKitIsPublicAndAllSubjectAndAttributesCustomized_ThenGetKitCustomSubject() {
        var param = createParam(GetKitCustomSubjectUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        Attribute attribute = AttributeMother.attributeWithTitle("flexibility");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var subjectCustom = new KitCustomData.Subject(subject.getId(), 2);
        var attributeCustom = new KitCustomData.Attribute(attribute.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(List.of(subjectCustom), List.of(attributeCustom));
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", kit.getId(), kitCustomData);

        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(List.of(subject),
            1,
            1,
            "asc",
            "index",
            1);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadKitCustomPort.loadByIdAndKitId(param.getKitCustomId(), param.getKitId())).thenReturn(kitCustom);
        when(loadSubjectsPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomSubjectUseCase.Subject> resultPaginatedResponse = service.getKitCustomSubject(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomSubjectUseCase.Subject> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.id());
        assertEquals(expectedSubject.getTitle(), actualSubject.title());
        assertEquals(expectedSubject.getIndex(), actualSubject.index());
        assertEquals(subjectCustom.weight(), actualSubject.weight().customValue());
        assertEquals(expectedSubject.getWeight(), actualSubject.weight().defaultValue());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.attributes().size());

        var expectedAttribute = expectedSubject.getAttributes().getFirst();
        var actualAttribute = actualSubject.attributes().getFirst();
        assertNotNull(actualAttribute);
        assertEquals(expectedAttribute.getId(), actualAttribute.id());
        assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
        assertEquals(expectedAttribute.getIndex(), actualAttribute.index());
        assertEquals(attributeCustom.weight(), actualAttribute.weight().customValue());
        assertEquals(expectedAttribute.getWeight(), actualAttribute.weight().defaultValue());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testGetKitCustomSubject_WhenKitIsPublicAndJustOneAttributeCustomized_ThenGetKitCustomSubject() {
        var param = createParam(GetKitCustomSubjectUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        Attribute flexibilityAttr = AttributeMother.attributeWithTitle("flexibility");
        Attribute maintainabilityAttr = AttributeMother.attributeWithTitle("maintainability");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(flexibilityAttr, maintainabilityAttr));
        var flexibilityAttrCustom = new KitCustomData.Attribute(flexibilityAttr.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(null, List.of(flexibilityAttrCustom));
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", kit.getId(), kitCustomData);

        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(List.of(subject),
            1,
            1,
            "asc",
            "index",
            1);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadKitCustomPort.loadByIdAndKitId(param.getKitCustomId(), param.getKitId())).thenReturn(kitCustom);
        when(loadSubjectsPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomSubjectUseCase.Subject> resultPaginatedResponse = service.getKitCustomSubject(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomSubjectUseCase.Subject> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.id());
        assertEquals(expectedSubject.getTitle(), actualSubject.title());
        assertEquals(expectedSubject.getIndex(), actualSubject.index());
        assertEquals(expectedSubject.getWeight(), actualSubject.weight().defaultValue());
        assertNull(actualSubject.weight().customValue());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.attributes().size());

        var expectedFlexibilityAttr = expectedSubject.getAttributes().getFirst();
        var actualFlexibilityAttr = actualSubject.attributes().getFirst();
        assertNotNull(actualFlexibilityAttr);
        assertEquals(expectedFlexibilityAttr.getId(), actualFlexibilityAttr.id());
        assertEquals(expectedFlexibilityAttr.getTitle(), actualFlexibilityAttr.title());
        assertEquals(expectedFlexibilityAttr.getIndex(), actualFlexibilityAttr.index());
        assertEquals(flexibilityAttrCustom.weight(), actualFlexibilityAttr.weight().customValue());
        assertEquals(expectedFlexibilityAttr.getWeight(), actualFlexibilityAttr.weight().defaultValue());

        var expectedMaintainabilityAttr = expectedSubject.getAttributes().get(1);
        var actualMaintainabilityAttr = actualSubject.attributes().get(1);
        assertNotNull(actualMaintainabilityAttr);
        assertEquals(expectedMaintainabilityAttr.getId(), actualMaintainabilityAttr.id());
        assertEquals(expectedMaintainabilityAttr.getTitle(), actualMaintainabilityAttr.title());
        assertEquals(expectedMaintainabilityAttr.getIndex(), actualMaintainabilityAttr.index());
        assertEquals(expectedMaintainabilityAttr.getWeight(), actualMaintainabilityAttr.weight().defaultValue());
        assertNull(actualMaintainabilityAttr.weight().customValue());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testGetKitCustomSubject_WhenKitIsPublicAndJustOneSubjectCustomized_ThenGetKitCustomSubject() {
        var param = createParam(GetKitCustomSubjectUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        Attribute flexibilityAttr = AttributeMother.attributeWithTitle("flexibility");
        Attribute maintainabilityAttr = AttributeMother.attributeWithTitle("maintainability");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(flexibilityAttr, maintainabilityAttr));
        var subjectCustom = new KitCustomData.Subject(subject.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(List.of(subjectCustom), new ArrayList<>());
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", kit.getId(), kitCustomData);

        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(List.of(subject),
            1,
            1,
            "asc",
            "index",
            1);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadKitCustomPort.loadByIdAndKitId(param.getKitCustomId(), param.getKitId())).thenReturn(kitCustom);
        when(loadSubjectsPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomSubjectUseCase.Subject> resultPaginatedResponse = service.getKitCustomSubject(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomSubjectUseCase.Subject> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.id());
        assertEquals(expectedSubject.getTitle(), actualSubject.title());
        assertEquals(expectedSubject.getIndex(), actualSubject.index());
        assertEquals(subjectCustom.weight(), actualSubject.weight().customValue());
        assertEquals(expectedSubject.getWeight(), actualSubject.weight().defaultValue());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.attributes().size());

        for (int i = 0; i < expectedSubject.getAttributes().size(); i++) {
            var expectedAttribute = expectedSubject.getAttributes().get(i);
            var actualAttribute = actualSubject.attributes().get(i);
            assertNotNull(actualAttribute);
            assertEquals(expectedAttribute.getId(), actualAttribute.id());
            assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
            assertEquals(expectedAttribute.getIndex(), actualAttribute.index());
            assertEquals(expectedAttribute.getWeight(), actualAttribute.weight().defaultValue());
            assertNull(actualAttribute.weight().customValue());
        }

        verifyNoInteractions(checkKitUserAccessPort);
    }

    private GetKitCustomSubjectUseCase.Param createParam(Consumer<GetKitCustomSubjectUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetKitCustomSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomSubjectUseCase.Param.builder()
            .kitId(1L)
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(2);
    }
}
