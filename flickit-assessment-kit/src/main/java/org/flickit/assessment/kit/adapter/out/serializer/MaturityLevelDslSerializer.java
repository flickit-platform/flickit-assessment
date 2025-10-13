package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;

import java.io.IOException;
import java.util.stream.Collectors;

public class MaturityLevelDslSerializer extends JsonSerializer<MaturityLevelDslModel> {

    @Override
    public void serialize(MaturityLevelDslModel level, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("level ").append(level.getCode()).append(" {\n");
        sb.append("    title: \"").append(level.getTitle()).append("\"\n");
        sb.append("    description: \"").append(level.getDescription()).append("\"\n");
        sb.append("    value: ").append(level.getValue()).append("\n");

        if (level.getCompetencesCodeToValueMap() != null && !level.getCompetencesCodeToValueMap().isEmpty()) {
            String competencesStr = level.getCompetencesCodeToValueMap().entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue() + "%")
                .collect(Collectors.joining(", "));
            sb.append("    competence: [").append(competencesStr).append("]\n");
        }

        sb.append("}\n");
        gen.writeRawValue(sb.toString());
    }
}
