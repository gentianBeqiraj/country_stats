package ch.bbw.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiCityResponse {

  Boolean error;
  String msg;
  List<CityResponse> data = new ArrayList<>();
}
