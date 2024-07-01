package ch.bbw.service;

import ch.bbw.dtos.ApiResponse;
import ch.bbw.dtos.CountryInfoResponse;
import ch.bbw.util.APIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CountryInfoService {

  private final APIClient apiClient;

  @Autowired
  public CountryInfoService() {
    this.apiClient = new APIClient("https://countriesnow.space/api/v0.1", 10);
  }

  public CountryInfoResponse getCountryInfo(String country)
      throws IOException, InterruptedException {

    return (CountryInfoResponse) getApiResponse().getData().stream()
        .filter(infoResponse -> country.equalsIgnoreCase(infoResponse.getName()));
  }

  private ApiResponse<CountryInfoResponse> getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/info?returns=currency,flag,dialCode,capital";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiResponse.class);
  }
}

