package ch.bbw.dtos;

import ch.bbw.util.deserializers.StringToLongDeserializer;
import ch.bbw.util.deserializers.StringToIntDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class PopulationCountResponse {

  @JsonDeserialize(using = StringToIntDeserializer.class)
  Integer year;

  @JsonDeserialize(using = StringToLongDeserializer.class)
  Long value;

  String sex;
  String reliabilty;
}
