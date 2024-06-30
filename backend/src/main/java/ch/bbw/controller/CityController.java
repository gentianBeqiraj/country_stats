package ch.bbw.controller;

import ch.bbw.dtos.CityResponse;
import ch.bbw.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cities")
public class CityController {

  private final CityService cityService;

  @Autowired
  public CityController(CityService cityService) {
    this.cityService = cityService;
  }

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping
  public String getCities(@RequestParam("country") String country, Model model) {
    try {
      List<CityResponse> filteredCities = cityService.getCitiesByCountry(country);
      model.addAttribute("country", country);
      model.addAttribute("cities", filteredCities);
      return "countries";
    } catch (Exception e) {
      model.addAttribute("error", "Unable to fetch cities at this time.");
      return "error";
    }
  }
}
