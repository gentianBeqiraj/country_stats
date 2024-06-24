package ch.bbw.controller;

import ch.bbw.dtos.CityResponse;
import ch.bbw.dtos.CountryResponse;
import ch.bbw.util.APIClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cities")
public class CityController {

  final String BASE_URL = "https://countriesnow.space/api/v0.1";
  final Integer TIMEOUT = 10;
  final APIClient client = new APIClient(BASE_URL, TIMEOUT);

  @GetMapping("/")
  public String index() {
    return "index";
  }

  private CountryResponse getCountryResponse() {
    final String endpoint = "countries/population/cities/";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    CountryResponse countryResponse = new CountryResponse();

    try {
      countryResponse = client.get(endpoint, headers, CountryResponse.class);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return countryResponse;
  }

  @GetMapping
  public String getCities(@RequestParam("country") String country, Model model) {
    CountryResponse countryResponse = getCountryResponse();

    List<CityResponse> filteredCities = countryResponse.getData().stream()
        .filter(cityResponse -> country.equalsIgnoreCase(cityResponse.getCountry()))
        .toList();

    model.addAttribute("country", country);
    model.addAttribute("cities", filteredCities);
    return "countries";
  }
}
