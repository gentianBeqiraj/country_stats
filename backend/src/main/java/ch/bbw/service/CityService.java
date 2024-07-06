package ch.bbw.service;

import ch.bbw.dtos.CityResponse;
import ch.bbw.dtos.ApiCityResponse;
import ch.bbw.util.APIClient;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling city-related operations.
 */
@Service
public class CityService {

  private final APIClient apiClient;

  /**
   * Constructor for CityService.
   * Initializes the API client with a base URL and a timeout.
   */
  @Autowired
  public CityService() {
    this.apiClient = new APIClient("https://countriesnow.space/api/v0.1", 10);
  }

  /**
   * Fetches and returns a list of cities for the specified country, sorted according to the given sort order.
   *
   * @param country   the name of the country
   * @param sortOrder the sort order for the city list (nameAsc, nameDesc, populationAsc, populationDesc, yearAsc, yearDesc)
   * @return a list of CityResponse objects
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
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

  /**
   * Generates a bar chart image for the population of the given cities and returns it as a Base64 encoded string.
   *
   * @param cities the list of cities to include in the chart
   * @return a Base64 encoded string of the chart image, or null if an error occurs
   */
  public String getCityChart(List<CityResponse> cities) {
    try {
      Map<String, Long> cityPopulationMap = cities.stream()
          .filter(cityResponse -> !cityResponse.getPopulationCounts().isEmpty())
          .sorted((c1, c2) -> Long.compare(
              c2.getPopulationCounts().getFirst().getValue(),
              c1.getPopulationCounts().getFirst().getValue()))
          .limit(5)
          .collect(Collectors.toMap(
              CityResponse::getCity,
              cityResponse -> cityResponse.getPopulationCounts().getFirst().getValue()
          ));

      DefaultCategoryDataset dataset = createDataset(cityPopulationMap);

      JFreeChart barChart = ChartFactory.createBarChart(
          "Biggest Cities",
          "Cities",
          "Population count",
          dataset,
          PlotOrientation.VERTICAL,
          false, true, false);

      CategoryPlot plot = (CategoryPlot) barChart.getPlot();
      BarRenderer renderer = (BarRenderer) plot.getRenderer();
      renderer.setSeriesPaint(0, new java.awt.Color(79, 129, 189));

      BufferedImage chartImage = barChart.createBufferedImage(800, 600);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(chartImage, "png", baos);

      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      System.out.println("Error while creating chart image:" + e);
      return null;
    }
  }

  /**
   * Fetches the API response containing city data.
   *
   * @return an ApiCityResponse object containing the city data
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  private ApiCityResponse getApiResponse() throws IOException, InterruptedException {
    final String endpoint = "countries/population/cities/";

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return apiClient.get(endpoint, headers, ApiCityResponse.class);
  }

  /**
   * Creates a dataset for the bar chart from the given city population map.
   *
   * @param cityPopulationMap a map containing city names as keys and their population counts as values
   * @return a DefaultCategoryDataset object for the bar chart
   */
  private DefaultCategoryDataset createDataset(Map<String, Long> cityPopulationMap) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    cityPopulationMap.forEach((city, population) ->
        dataset.addValue(population, "Values", city));

    return dataset;
  }
}
