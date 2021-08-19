package org.qwertech.objectmapperplay;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

@Slf4j
public class ObjectMapperTest {


  @SneakyThrows
  @Test
  public void testObjectMapperIsAbleToConvertPropertyNameAndBooleanAndEnum() {
    var object = new TestConvertBoolean(true, NameValue.ONE);
    ObjectMapper objectMapper = new ObjectMapper()

        .addMixIn(TestConvertBoolean.class, TestConvertBooleanMixin.class);
    String string = objectMapper
        .setSerializationInclusion(Include.NON_NULL)
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(object);
    JSONAssert.assertEquals("{\"Булево значение\":\"Да\",\"Значение из перечисления\":\"first\"}", string, false);
    log.info(string);
  }

  @AllArgsConstructor
  @Getter
  public enum NameValue {
    ONE("first"),
    TWO("second");
    private String name;
  }


  @AllArgsConstructor
  @Getter
  public static class TestConvertBoolean {

    private Boolean value;
    private NameValue name;

  }

  @AllArgsConstructor
  public abstract static class TestConvertBooleanMixin {

    @JsonProperty("Булево значение")
    @JsonSerialize(using = CustomBoolSerializer.class)
    private Boolean value;
    @JsonProperty("Значение из перечисления")
    @JsonSerialize(using = CustomNameValueSerializer.class)
    private NameValue name;
  }


  private static class CustomBoolSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (Objects.isNull(value)) {
        gen.writeNull();
        return;
      }
      gen.writeString(value ? "Да" : "Нет");
    }
  }

  public static class CustomNameValueSerializer extends JsonSerializer<NameValue> {

    @Override
    public void serialize(NameValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (Objects.isNull(value)) {
        gen.writeNull();
        return;
      }
      gen.writeString(value.getName());
    }
  }
}
