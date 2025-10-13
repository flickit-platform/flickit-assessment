package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;

import java.io.IOException;
import java.util.stream.Collectors;

public class QuestionDslSerializer extends JsonSerializer<QuestionDslModel> {

    @Override
    public void serialize(QuestionDslModel question, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String impacts = question.getQuestionImpacts().stream()
            .map(impact -> "    affects " + impact.getAttributeCode()
                + " on level " + impact.getMaturityLevel().getCode()
                + " with weight " + impact.getWeight())
            .collect(Collectors.joining("\n"));

        String s =
            "question " + question.getCode() + " {\n" +
                "    questionnaire: " + question.getQuestionnaireCode() + "\n" +
                "    hint: \"" + (question.getDescription() == null ? " " : question.getDescription()) + "\"\n" +
                "    title: \"" + question.getTitle() + "\"\n" +
                "    answerRange: " + question.getAnswerRangeCode() + "\n" +
                impacts + "\n" +
                "}\n";

        gen.writeRawValue(s);
    }
}
