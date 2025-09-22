package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.domain.AssessmentMode;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment assessment() {
        return assessmentWithKitLanguageAndMode(KitLanguage.EN, AssessmentMode.ADVANCED);
    }

    public static Assessment assessmentWithMode(AssessmentMode mode) {
        counter++;
        return assessmentWithKitLanguageAndMode(KitLanguage.EN, mode);
    }

    public static Assessment assessmentWithKitLanguage(KitLanguage language) {
        counter++;
        return assessmentWithKitLanguageAndMode(language, AssessmentMode.ADVANCED);
    }

    public static Assessment assessmentWithKitLanguageAndMode(KitLanguage language, AssessmentMode mode) {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            "Short title" + counter,
            AssessmentKitMother.kitWithLanguage(language),
            SpaceMother.createBasicSpace(),
            234L,
            mode,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false,
            UUID.randomUUID()
        );
    }

    public static Assessment assessmentWithoutActiveVersion() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            "Short title" + counter,
            AssessmentKitMother.AssessmentKitWithoutActiveKitVersion(),
            SpaceMother.createBasicSpace(),
            234L,
            AssessmentMode.ADVANCED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false,
            UUID.randomUUID()
        );
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId) {
        return assessmentListItem(spaceId, kitId, Boolean.FALSE, AssessmentMode.ADVANCED);
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId, boolean manageable, AssessmentMode mode) {
        counter++;
        return new AssessmentListItem(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            new AssessmentListItem.Kit(kitId, "kitTitle" + kitId, 2),
            new AssessmentListItem.Space(spaceId, "spaceTitle"),
            LocalDateTime.now(),
            new AssessmentListItem.MaturityLevel(counter, "levelTitle" + counter, 1, 2),
            new Random().nextDouble() * 100,
            Boolean.TRUE,
            Boolean.TRUE,
            KitLanguage.FA,
            mode,
            manageable,
            Boolean.FALSE);
    }
}
