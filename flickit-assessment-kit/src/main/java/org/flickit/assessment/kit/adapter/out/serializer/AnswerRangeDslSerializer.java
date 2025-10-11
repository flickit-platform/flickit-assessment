package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;

import java.io.IOException;
import java.util.stream.Collectors;

public class AnswerRangeDslSerializer extends JsonSerializer<AnswerRangeDslModel> {

    @Override
    public void serialize(AnswerRangeDslModel answerRange, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String options = answerRange.getAnswerOptions().stream()
            .map(opt -> "\"" + opt.getCaption() + "\"")
            .collect(Collectors.joining(", "));

        String values = answerRange.getAnswerOptions().stream()
            .map(opt -> String.valueOf(opt.getValue()))
            .collect(Collectors.joining(", "));

        String s =
            "answerRange " + answerRange.getCode() + " {\n" +
                "    title: \"" + answerRange.getCode() + "\"\n" +
                "    options: " + options + "    with values [" + values + "]\n" +
                "}\n";

        gen.writeRawValue(s);
    }
}
