package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;

import java.io.IOException;

public class AttributeDslSerializer extends JsonSerializer<AttributeDslModel> {

    @Override
    public void serialize(AttributeDslModel attr, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String s = "attribute " + attr.getCode() + " {\n" +
            "    title: \"" + attr.getTitle() + "\"\n" +
            "    description: \"" + attr.getDescription() + "\"\n" +
            "    subject: " + attr.getSubjectCode() + "\n" +
            "}\n";

        gen.writeRawValue(s);
    }
}
