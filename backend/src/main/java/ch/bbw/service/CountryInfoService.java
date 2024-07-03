package ch.bbw.service;

import ch.bbw.dtos.ApiInfoResponse;
import ch.bbw.dtos.CountryInfoResponse;
import ch.bbw.util.APIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling country information related operations.
 */
@Service
public class CountryInfoService {

  private final APIClient apiClient;

  /**
   * Constructor for CountryInfoService.
   * Initializes the API client with a base URL and a timeout.
   */
  @Autowired
  public CountryInfoService() {
    this.apiClient = new APIClient("https://countriesnow.space/api/v0.1", 10);
  }

  /**
   * Fetches and returns information about the specified country.
   *
   * @param country the name of the country
   * @return a CountryInfoResponse object containing information about the country, or null if not found
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public CountryInfoResponse getCountryInfo(String country)
      throws IOException, InterruptedException {

    return getApiResponse().getData().stream()
        .filter(infoResponse -> country.equalsIgnoreCase(infoResponse.getName()))
        .findFirst().orElse(null);
  }

  /**
   * Fetches the API response containing country information.
   *
   * @return an ApiInfoResponse object containing country information
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  private ApiInfoResponse getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/info?returns=currency,flag,dialCode,capital";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiInfoResponse.class);
  }
}
