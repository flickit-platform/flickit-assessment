package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

import java.io.IOException;

public class QuestionnaireDslSerializer extends JsonSerializer<QuestionnaireDslModel> {

    @Override
    public void serialize(QuestionnaireDslModel questionnaire, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String s = "questionnaire " + questionnaire.getCode() + " {\n" +
            "    title: \"" + questionnaire.getTitle() + "\"\n" +
            "    description: \"" + questionnaire.getDescription() + "\"\n" +
            "}\n";

        gen.writeRawValue(s);
    }
}
