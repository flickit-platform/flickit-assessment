package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;

import java.io.IOException;

public class SubjectDslSerializer extends JsonSerializer<SubjectDslModel> {

    @Override
    public void serialize(SubjectDslModel subject, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String s = "subject " + subject.getCode() + " {\n" +
            "    title: \"" + subject.getTitle() + "\"\n" +
            "    description: \"" + subject.getDescription() + "\"\n" +
            "}\n";

        gen.writeRawValue(s);
    }
}
