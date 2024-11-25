package org.flickit.assessment.kit.application.service.kitcustom;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_CUSTOM_UNRELATED_ATTRIBUTE_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_CUSTOM_UNRELATED_SUBJECT_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitCustomServiceTest {

    @InjectMocks
    private UpdateKitCustomService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private UpdateKitCustomPort updateKitCustomPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Captor
    private ArgumentCaptor<UpdateKitCustomPort.Param> portParamCaptor;

    @Test
    void testUpdateKitCustom_WhenKitIsPrivateAndUserHasNotAccess_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(kit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPrivateAndCurrentUserHasAccessToKit_ThenCreateKitCustom() {
        var attribute = AttributeMother.attributeWithTitle("flexibility");
        var subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var kit = AssessmentKitMother.kitWithSubjects(List.of(subject), true);
        var customSubject = new UpdateKitCustomService.Param.KitCustomData.CustomSubject(subject.getId(), 1);
        var customAttribute = new UpdateKitCustomService.Param.KitCustomData.CustomAttribute(attribute.getId(), 5);
        var customData = new UpdateKitCustomService.Param.KitCustomData(List.of(customSubject), List.of(customAttribute));
        var param = createParam(b -> b.customData(customData));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));

        service.updateKitCustom(param);
        assertUpdateKitCustomPortParamMapping(param);
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPublic_ThenCreateKitCustom() {
        var attribute = AttributeMother.attributeWithTitle("flexibility");
        var subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var kit = AssessmentKitMother.kitWithSubjects(List.of(subject));
        var customSubject = new UpdateKitCustomService.Param.KitCustomData.CustomSubject(subject.getId(), 1);
        var customAttribute = new UpdateKitCustomService.Param.KitCustomData.CustomAttribute(attribute.getId(), 5);
        var customData = new UpdateKitCustomService.Param.KitCustomData(List.of(customSubject), List.of(customAttribute));
        var param = createParam(b -> b.customData(customData));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));

        service.updateKitCustom(param);
        assertUpdateKitCustomPortParamMapping(param);

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPublicButSubjectCustomIsNotRelatedToKit_ThenThrowValidationException() {
        var attribute = AttributeMother.attributeWithTitle("flexibility");
        var subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var kit = AssessmentKitMother.kitWithSubjects(List.of(subject));
        var customSubject = new UpdateKitCustomService.Param.KitCustomData.CustomSubject(1L, 1);
        var customAttribute = new UpdateKitCustomService.Param.KitCustomData.CustomAttribute(attribute.getId(), 5);
        var customData = new UpdateKitCustomService.Param.KitCustomData(List.of(customSubject), List.of(customAttribute));
        var param = createParam(b -> b.customData(customData));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));

        var throwable = assertThrows(ValidationException.class, () -> service.updateKitCustom(param));
        assertEquals(UPDATE_KIT_CUSTOM_UNRELATED_SUBJECT_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(checkKitUserAccessPort, updateKitCustomPort);
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPublicButAttributeCustomIsNotRelatedToKit_ThenThrowValidationException() {
        var attribute = AttributeMother.attributeWithTitle("flexibility");
        var subject = SubjectMother.subjectWithAttributes("software", List.of(attribute));
        var kit = AssessmentKitMother.kitWithSubjects(List.of(subject));
        var customSubject = new UpdateKitCustomService.Param.KitCustomData.CustomSubject(subject.getId(), 1);
        var customAttribute = new UpdateKitCustomService.Param.KitCustomData.CustomAttribute(1L, 5);
        var customData = new UpdateKitCustomService.Param.KitCustomData(List.of(customSubject), List.of(customAttribute));
        var param = createParam(b -> b.customData(customData));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));

        var throwable = assertThrows(ValidationException.class, () -> service.updateKitCustom(param));
        assertEquals(UPDATE_KIT_CUSTOM_UNRELATED_ATTRIBUTE_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(checkKitUserAccessPort, updateKitCustomPort);
    }

    private void assertUpdateKitCustomPortParamMapping(UpdateKitCustomUseCase.Param param) {
        verify(updateKitCustomPort).update(portParamCaptor.capture());
        assertNotNull(portParamCaptor.getValue());
        assertEquals(param.getKitId(), portParamCaptor.getValue().kitId());
        assertEquals(param.getTitle(), portParamCaptor.getValue().title());
        assertEquals("title", portParamCaptor.getValue().code());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());

        var actualSubjects = portParamCaptor.getValue().customData().subjects();
        var expectedSubjects = param.getCustomData().customSubjects();
        Assertions.assertThat(actualSubjects)
            .zipSatisfy(expectedSubjects, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getWeight(), actual.weight());
            });

        var actualAttributes = portParamCaptor.getValue().customData().attributes();
        var expectedAttributes = param.getCustomData().customAttributes();
        Assertions.assertThat(actualAttributes)
            .zipSatisfy(expectedAttributes, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getWeight(), actual.weight());
            });

    }

    private UpdateKitCustomUseCase.Param createParam(Consumer<UpdateKitCustomUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitCustomUseCase.Param.builder()
            .kitCustomId(12L)
            .kitId(1L)
            .title("title")
            .customData(createCustomDataParam(UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder::build))
            .currentUserId(UUID.randomUUID());
    }

    private UpdateKitCustomUseCase.Param.KitCustomData createCustomDataParam(Consumer<UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder> changer) {
        var paramBuilder = KitCustomDataBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder KitCustomDataBuilder() {
        var customSubject = new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(1L, 1);
        var customAttribute = new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(1L, 1);
        return UpdateKitCustomUseCase.Param.KitCustomData.builder()
            .customSubjects(List.of(customSubject))
            .customAttributes(List.of(customAttribute));
    }
}
