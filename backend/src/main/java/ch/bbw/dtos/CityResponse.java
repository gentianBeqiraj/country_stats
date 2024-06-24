package ch.bbw.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CityResponse {

  String city;
  String country;
  List<PopulationCountResponse> populationCounts = new ArrayList<>();
}
