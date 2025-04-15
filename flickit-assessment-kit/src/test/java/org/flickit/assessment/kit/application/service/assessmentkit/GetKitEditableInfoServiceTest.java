package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.KitTagMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitEditableInfoServiceTest {

    @InjectMocks
    private GetKitEditableInfoService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadKitTagListPort loadKitTagListPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadKitLanguagesPort loadKitLanguagesPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetKitEditableInfo_KitNotFound_ErrorMessage() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(kitId, currentUserId);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAssessmentKitPort.load(kitId)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
        verifyNoInteractions(loadKitTagListPort, loadKitLanguagesPort);
    }

    @Test
    void testGetKitEditableInfo_ValidInput_ValidResults() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(kitId, currentUserId);

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> tags = List.of(KitTagMother.createKitTag("security"));
        ExpertGroup expertGroup = new ExpertGroup(1L, null, null, currentUserId);
        List<KitLanguage> languages = List.of(KitLanguage.EN, KitLanguage.FA);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAssessmentKitPort.load(kitId)).thenReturn(assessmentKit);
        when(loadKitTagListPort.loadByKitId(kitId)).thenReturn(tags);
        when(loadKitLanguagesPort.loadByKitId(kitId)).thenReturn(languages);

        GetKitEditableInfoUseCase.KitEditableInfo kitEditableInfo = service.getKitEditableInfo(param);

        assertEquals(assessmentKit.getId(), kitEditableInfo.id());
        assertEquals(assessmentKit.getTitle(), kitEditableInfo.title());
        assertEquals(assessmentKit.getSummary(), kitEditableInfo.summary());
        assertEquals(assessmentKit.getLanguage().getCode(), kitEditableInfo.mainLanguage().code());
        assertEquals(assessmentKit.getLanguage().getTitle(), kitEditableInfo.mainLanguage().title());
        assertEquals(assessmentKit.isPublished(), kitEditableInfo.published());
        assertEquals(assessmentKit.isPrivate(), kitEditableInfo.isPrivate());
        assertEquals(0, kitEditableInfo.price());
        assertEquals(assessmentKit.getAbout(), kitEditableInfo.about());
        assertEquals(tags.size(), kitEditableInfo.tags().size());
        assertEquals(languages.size(), kitEditableInfo.languages().size());
        assertTrue(kitEditableInfo.editable());
    }

    @Test
    void testGetKitEditableInfo_CurrentUserIsNotExpertGroupMember_ErrorMessage() {
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
