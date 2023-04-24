package de.wejago.hichi2influx.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.wejago.hichi2influx.dto.SensorEntry2;
import de.wejago.hichi2influx.dto.SensorMeasurement2;
import java.io.IOException;
import java.time.LocalDateTime;

public class SensorEntryDeserializer extends JsonDeserializer<SensorEntry2> {

    @Override
    public SensorEntry2 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String time = node.get("Time").asText();
        LocalDateTime localDateTime = LocalDateTime.parse(time);

        JsonNode emptyKeyNode = node.get("");
        SensorMeasurement2 sml = jp.getCodec().treeToValue(emptyKeyNode, SensorMeasurement2.class);

        SensorEntry2 sensorEntry = new SensorEntry2();
        sensorEntry.setTime(localDateTime.toString());
        sensorEntry.setSml(sml);

        return sensorEntry;
    }
}
