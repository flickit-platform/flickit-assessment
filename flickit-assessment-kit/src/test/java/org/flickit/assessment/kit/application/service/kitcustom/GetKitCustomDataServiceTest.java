package org.flickit.assessment.kit.application.service.kitcustom;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomDataUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
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
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_CUSTOM_DATA_KIT_CUSTOM_ID_INVALID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitCustomDataServiceTest {

    @InjectMocks
    private GetKitCustomDataService service;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private LoadKitCustomPort loadKitCustomPort;

    @Test
    void testGetCustomData_WhenKitIsPrivateAndCurrentUserHasNoAccessToKit_ThenThrowAccessDeniedException() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.getKitCustomData(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());

        verifyNoInteractions(loadSubjectPort, loadKitCustomPort);
    }

    @Test
    void testGetCustomData_WhenKitIsPublicButRequestedKitCustomIsNotValidForKit_ThenThrowValidationException() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        Attribute attribute = AttributeMother.attributeWithTitle("flexibility");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var subjectCustom = new KitCustomData.Subject(subject.getId(), 2);
        var attributeCustom = new KitCustomData.Attribute(attribute.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(List.of(subjectCustom), List.of(attributeCustom));
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", 10, kitCustomData);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadKitCustomPort.loadById(param.getKitCustomId())).thenReturn(kitCustom);

        var throwable = assertThrows(ValidationException.class, () -> service.getKitCustomData(param));
        assertEquals(GET_KIT_CUSTOM_DATA_KIT_CUSTOM_ID_INVALID, throwable.getMessageKey());

        verifyNoInteractions(checkKitUserAccessPort, loadSubjectPort);
    }

    @Test
    void testGetCustomData_WhenKitIsPrivateAndCurrentUserHasAccessToKitAndAllSubjectAndAttributesCustomized_ThenGetKitCustomData() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
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
        when(loadKitCustomPort.loadById(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadSubjectPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomDataUseCase.Result> resultPaginatedResponse = service.getKitCustomData(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomDataUseCase.Result> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedItem = paginatedResponse.getItems().getFirst();
        var actualItem = actualItems.getFirst();
        assertNotNull(actualItem);
        assertEquals(expectedItem.getId(), actualItem.subject().id());
        assertEquals(expectedItem.getTitle(), actualItem.subject().title());
        assertEquals(subjectCustom.weight(), actualItem.subject().weight());
        assertTrue(actualItem.subject().customized());
        assertEquals(expectedItem.getAttributes().size(), actualItem.subject().attributes().size());

        var expectedAttribute = expectedItem.getAttributes().getFirst();
        var actualAttribute = actualItem.subject().attributes().getFirst();
        assertNotNull(actualAttribute);
        assertEquals(expectedAttribute.getId(), actualAttribute.id());
        assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
        assertEquals(attributeCustom.weight(), actualAttribute.weight());
        assertTrue(actualAttribute.customized());
    }

    @Test
    void testGetKitCustomData_WhenKitIsPublicAndAllSubjectAndAttributesCustomized_ThenGetKitCustomData() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
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
        when(loadKitCustomPort.loadById(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadSubjectPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomDataUseCase.Result> resultPaginatedResponse = service.getKitCustomData(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomDataUseCase.Result> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.subject().id());
        assertEquals(expectedSubject.getTitle(), actualSubject.subject().title());
        assertEquals(subjectCustom.weight(), actualSubject.subject().weight());
        assertTrue(actualSubject.subject().customized());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.subject().attributes().size());

        var expectedAttribute = expectedSubject.getAttributes().getFirst();
        var actualAttribute = actualSubject.subject().attributes().getFirst();
        assertNotNull(actualAttribute);
        assertEquals(expectedAttribute.getId(), actualAttribute.id());
        assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
        assertEquals(attributeCustom.weight(), actualAttribute.weight());
        assertTrue(actualAttribute.customized());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testGetKitCustomData_WhenKitIsPublicAndJustOneAttributeCustomized_ThenGetKitCustomData() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        Attribute flexibilityAttr = AttributeMother.attributeWithTitle("flexibility");
        Attribute maintainabilityAttr = AttributeMother.attributeWithTitle("maintainability");
        Subject subject = SubjectMother.subjectWithAttributes("software", List.of(flexibilityAttr, maintainabilityAttr));
        var flexibilityAttrCustom = new KitCustomData.Attribute(flexibilityAttr.getId(), 2);
        KitCustomData kitCustomData = new KitCustomData(new ArrayList<>(), List.of(flexibilityAttrCustom));
        LoadKitCustomPort.Result kitCustom = new LoadKitCustomPort.Result(1, "custom", kit.getId(), kitCustomData);

        PaginatedResponse<Subject> paginatedResponse = new PaginatedResponse<>(List.of(subject),
            1,
            1,
            "asc",
            "index",
            1);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadKitCustomPort.loadById(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadSubjectPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomDataUseCase.Result> resultPaginatedResponse = service.getKitCustomData(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomDataUseCase.Result> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.subject().id());
        assertEquals(expectedSubject.getTitle(), actualSubject.subject().title());
        assertEquals(expectedSubject.getWeight(), actualSubject.subject().weight());
        assertFalse(actualSubject.subject().customized());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.subject().attributes().size());

        var expectedFlexibilityAttr = expectedSubject.getAttributes().getFirst();
        var actualFlexibilityAttr = actualSubject.subject().attributes().getFirst();
        assertNotNull(actualFlexibilityAttr);
        assertEquals(expectedFlexibilityAttr.getId(), actualFlexibilityAttr.id());
        assertEquals(expectedFlexibilityAttr.getTitle(), actualFlexibilityAttr.title());
        assertEquals(flexibilityAttrCustom.weight(), actualFlexibilityAttr.weight());
        assertTrue(actualFlexibilityAttr.customized());

        var expectedMaintainabilityAttr = expectedSubject.getAttributes().get(1);
        var actualMaintainabilityAttr = actualSubject.subject().attributes().get(1);
        assertNotNull(actualMaintainabilityAttr);
        assertEquals(expectedMaintainabilityAttr.getId(), actualMaintainabilityAttr.id());
        assertEquals(expectedMaintainabilityAttr.getTitle(), actualMaintainabilityAttr.title());
        assertEquals(expectedMaintainabilityAttr.getWeight(), actualMaintainabilityAttr.weight());
        assertFalse(actualMaintainabilityAttr.customized());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testGetKitCustomData_WhenKitIsPublicAndJustOneSubjectCustomized_ThenGetKitCustomData() {
        var param = createParam(GetKitCustomDataUseCase.Param.ParamBuilder::build);
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
        when(loadKitCustomPort.loadById(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadSubjectPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize())).thenReturn(paginatedResponse);

        PaginatedResponse<GetKitCustomDataUseCase.Result> resultPaginatedResponse = service.getKitCustomData(param);
        assertNotNull(resultPaginatedResponse);
        List<GetKitCustomDataUseCase.Result> actualItems = resultPaginatedResponse.getItems();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());

        var expectedSubject = paginatedResponse.getItems().getFirst();
        var actualSubject = actualItems.getFirst();
        assertNotNull(actualSubject);
        assertEquals(expectedSubject.getId(), actualSubject.subject().id());
        assertEquals(expectedSubject.getTitle(), actualSubject.subject().title());
        assertEquals(subjectCustom.weight(), actualSubject.subject().weight());
        assertTrue(actualSubject.subject().customized());
        assertEquals(expectedSubject.getAttributes().size(), actualSubject.subject().attributes().size());

        for (int i = 0; i < expectedSubject.getAttributes().size(); i++) {
            var expectedAttribute = expectedSubject.getAttributes().get(i);
            var actualAttribute = actualSubject.subject().attributes().get(i);
            assertNotNull(actualAttribute);
            assertEquals(expectedAttribute.getId(), actualAttribute.id());
            assertEquals(expectedAttribute.getTitle(), actualAttribute.title());
            assertEquals(expectedAttribute.getWeight(), actualAttribute.weight());
            assertFalse(actualAttribute.customized());
        }

        verifyNoInteractions(checkKitUserAccessPort);
    }

    private GetKitCustomDataUseCase.Param createParam(Consumer<GetKitCustomDataUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetKitCustomDataUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomDataUseCase.Param.builder()
            .kitId(1L)
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(2);
    }
}
