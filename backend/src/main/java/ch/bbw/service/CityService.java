package ch.bbw.service;

import ch.bbw.dtos.CityResponse;
import ch.bbw.dtos.ApiResponse;
import ch.bbw.util.APIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CityService {

  private final APIClient apiClient;

  @Autowired
  public CityService() {
    this.apiClient = new APIClient("https://countriesnow.space/api/v0.1", 10);
  }

  public List<CityResponse> getCitiesByCountry(String country, String sortOrder)
      throws IOException, InterruptedException {

    List<CityResponse> cities = getApiResponse().getData().stream()
        .filter(cityResponse -> country.equalsIgnoreCase(cityResponse.getCountry()))
        .collect(Collectors.toList());

    switch (sortOrder) {
      case "nameAsc":
        cities.sort(Comparator.comparing(CityResponse::getCity));
        break;
      case "nameDesc":
        cities.sort(Comparator.comparing(CityResponse::getCity).reversed());
        break;
      case "populationAsc":
        cities.sort(Comparator.comparing(city -> city.getPopulationCounts().getFirst().getValue()));
        break;
      case "populationDesc":
        cities.sort(Comparator.comparing((CityResponse city) -> city.getPopulationCounts().getFirst().getValue()).reversed());
        break;
      case "yearAsc":
        cities.sort(Comparator.comparing(city -> city.getPopulationCounts().getFirst().getYear()));
        break;
      case "yearDesc":
        cities.sort(Comparator.comparing((CityResponse city) -> city.getPopulationCounts().getFirst().getYear()).reversed());
        break;
    }

    return cities;
  }

  private ApiResponse getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/population/cities/";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiResponse.class);
  }
}

