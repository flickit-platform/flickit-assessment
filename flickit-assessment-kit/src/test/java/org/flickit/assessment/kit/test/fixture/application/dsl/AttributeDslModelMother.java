package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;

import java.util.function.Consumer;

public class AttributeDslModelMother {

    public static AttributeDslModel domainToDslModel(Attribute attribute) {
        return domainToDslModel(attribute, b -> {});
    }

    public static AttributeDslModel domainToDslModel(Attribute attribute,
                                                     Consumer<AttributeDslModel.AttributeDslModelBuilder<?, ?>> changer) {

        var builder = domainToDslModelBuilder(attribute);
        changer.accept(builder);
        return builder.build();
    }

    private static AttributeDslModel.AttributeDslModelBuilder<?, ?> domainToDslModelBuilder(Attribute attribute) {
        return AttributeDslModel.builder()
            .code(attribute.getCode())
            .title(attribute.getTitle())
            .index(attribute.getIndex())
            .description(attribute.getDescription())
            .weight(attribute.getWeight());
    }
}
