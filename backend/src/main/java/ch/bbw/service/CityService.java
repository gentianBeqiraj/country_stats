package ch.bbw.service;

import ch.bbw.dtos.CityResponse;
import ch.bbw.dtos.ApiResponse;
import ch.bbw.util.APIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

  public List<CityResponse> getCitiesByCountry(String country) throws IOException, InterruptedException {
    ApiResponse response = getApiResponse();

    return response.getData().stream()
        .filter(cityResponse -> country.equalsIgnoreCase(cityResponse.getCountry()))
        .collect(Collectors.toList());
  }

  private ApiResponse getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/population/cities/";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiResponse.class);
  }
}

