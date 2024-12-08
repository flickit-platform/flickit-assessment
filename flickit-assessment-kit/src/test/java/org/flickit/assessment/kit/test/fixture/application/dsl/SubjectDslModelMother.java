package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;

import java.util.function.Consumer;

public class SubjectDslModelMother {

    public static SubjectDslModel domainToDslModel(Subject subject) {
        return domainToDslModel(subject, b -> {
        });
    }

    public static SubjectDslModel domainToDslModel(Subject subject,
                                                   Consumer<SubjectDslModel.SubjectDslModelBuilder<?, ?>> changer) {
        var builder = domainToDslModelBuilder(subject);
        changer.accept(builder);
        return builder.build();
    }

    private static SubjectDslModel.SubjectDslModelBuilder<?, ?> domainToDslModelBuilder(Subject subject) {
        return SubjectDslModel.builder()
            .code(subject.getCode())
            .title(subject.getTitle())
            .index(subject.getIndex())
            .description(subject.getDescription())
            .weight(1);
    }
}
