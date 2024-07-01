package ch.bbw.service;

import ch.bbw.dtos.ApiInfoResponse;
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

    return getApiResponse().getData().stream()
        .filter(infoResponse -> country.equalsIgnoreCase(infoResponse.getName()))
        .findFirst().orElse(null);
  }

  private ApiInfoResponse getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/info?returns=currency,flag,dialCode,capital";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiInfoResponse.class);
  }
}

