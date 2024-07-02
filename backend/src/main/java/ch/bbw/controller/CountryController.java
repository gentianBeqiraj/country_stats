package ch.bbw.controller;

import ch.bbw.dtos.CityResponse;
import ch.bbw.dtos.CountryInfoResponse;
import ch.bbw.service.CityService;
import ch.bbw.service.CountryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for handling country statistics related requests.
 */
@Controller
@RequestMapping("/country-stats")
public class CountryController {

  private final CityService cityService;
  private final CountryInfoService countryInfoService;

  /**
   * Constructor for CountryController.
   *
   * @param cityService         the service for city-related operations
   * @param countryInfoService  the service for country info-related operations
   */
  @Autowired
  public CountryController(CityService cityService, CountryInfoService countryInfoService) {
    this.cityService = cityService;
    this.countryInfoService = countryInfoService;
  }

  /**
   * Handles the root request for the country-stats endpoint.
   *
   * @return the name of the index view
   */
  @GetMapping("/")
  public String index() {
    return "index";
  }

  /**
   * Handles requests to fetch and display country statistics and cities.
   *
   * @param country   the name of the country
   * @param sortOrder the sort order for the city list (default is nameAsc)
   * @param model     the model to pass data to the view
   * @return the name of the view to be rendered
   */
  @GetMapping
  public String getCountryStats(
      @RequestParam("country") String country,
      @RequestParam(value = "sortOrder", defaultValue = "nameAsc") String sortOrder,
      Model model) {

    try {
      CountryInfoResponse countryInfo = countryInfoService.getCountryInfo(country);
      List<CityResponse> filteredCities = cityService.getCitiesByCountry(country, sortOrder);
      model.addAttribute("countryInfo", countryInfo);
      model.addAttribute("country", country);
      model.addAttribute("cities", filteredCities);
      return "country-stats";
    } catch (Exception e) {
      model.addAttribute("error", "Unable to fetch cities at this time.");
      return "error";
    }
  }
}
