package ch.bbw.dtos;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ApiResponse {

  Boolean error;
  String msg;
  List<CityResponse> data = new ArrayList<>();
}
