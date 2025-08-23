package org.flickit.assessment.common.application.domain.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KitCustomDataTest {

    @Test
    @SneakyThrows
    void testPropertyNamesShouldNotBeChanged() {
        String validJson = """
            {"subs":[{"id":1000,"w":1}],"atts":[{"id":200,"w":2}]}
            """;

        KitCustomData data = new ObjectMapper().readValue(validJson, KitCustomData.class);

        assertEquals(1, data.subjects().size());
        assertEquals(1, data.subjects().getFirst().weight());
        assertEquals(1000, data.subjects().getFirst().id());

        assertEquals(1, data.attributes().size());
        assertEquals(2, data.attributes().getFirst().weight());
        assertEquals(200, data.attributes().getFirst().id());
    }
}
