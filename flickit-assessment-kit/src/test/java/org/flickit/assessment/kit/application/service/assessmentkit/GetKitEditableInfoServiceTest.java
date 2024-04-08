package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitEditableInfoServiceTest {

    @InjectMocks
    private GetKitEditableInfoService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadKitTagsListPort loadKitTagsListPort;

    @Test
    void testGetKitStats_KitNotFound_ErrorMessage() {
        long kitId = 123L;
        GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(kitId);

        when(loadAssessmentKitPort.load(kitId)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_ValidInput_ValidResults() {
        long kitId = 123L;
        GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(kitId);

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> tags = List.of(new KitTag(1L, "Tag1"));

        when(loadAssessmentKitPort.load(kitId)).thenReturn(assessmentKit);
        when(loadKitTagsListPort.load(kitId)).thenReturn(tags);

        GetKitEditableInfoUseCase.KitEditableInfo kitStats = service.getKitEditableInfo(param);

        assertEquals(assessmentKit.getId(), kitStats.id());
        assertEquals(assessmentKit.getTitle(), kitStats.title());
        assertEquals(assessmentKit.getSummary(), kitStats.summary());
        assertEquals(assessmentKit.isPublished(), kitStats.isActive());
        assertEquals(0, kitStats.price());
        assertEquals(assessmentKit.getAbout(), kitStats.about());
        assertEquals(tags.size(), kitStats.tags().size());
    }
}
