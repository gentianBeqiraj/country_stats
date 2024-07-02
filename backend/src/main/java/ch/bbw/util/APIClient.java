package ch.bbw.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import ch.bbw.enums.Method;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A utility class for making HTTP requests to an API.
 * Provides methods for sending GET, POST, PUT, and DELETE requests.
 */
public class APIClient {
  private final String baseUrl;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  /**
   * Constructs an APIClient with the specified base URL and timeout.
   *
   * @param baseUrl the base URL for the API
   * @param timeout the timeout duration in seconds for the HTTP client
   */
  public APIClient(String baseUrl, Integer timeout) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(timeout))
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Builds an HTTP request with the specified method, endpoint, headers, body, and timeout.
   *
   * @param method   the HTTP method (GET, POST, PUT, DELETE)
   * @param endpoint the API endpoint
   * @param headers  the headers to include in the request
   * @param body     the body of the request (for POST and PUT requests)
   * @param timeout  the timeout duration for the request
   * @return the built HttpRequest
   */
  private HttpRequest buildRequest(Method method, String endpoint, Map<String, String> headers, String body, Duration timeout) {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(this.baseUrl + endpoint))
        .timeout(timeout);

    headers.forEach(builder::header);

    switch (method) {
      case GET:
        builder.GET();
        break;
      case POST:
        builder.POST(HttpRequest.BodyPublishers.ofString(body));
        break;
      case PUT:
        builder.PUT(HttpRequest.BodyPublishers.ofString(body));
        break;
      case DELETE:
        builder.DELETE();
        break;
    }

    return builder.build();
  }

  /**
   * Sends an HTTP request and parses the response to the specified type.
   *
   * @param request      the HttpRequest to send
   * @param responseType the class of the response type
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  private <T> T sendRequest(HttpRequest request, Class<T> responseType)
      throws IOException, InterruptedException {
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    int statusCode = response.statusCode();
    if (statusCode >= 200 && statusCode < 300) {
      return objectMapper.readValue(response.body(), responseType);
    } else {
      handleErrorResponse(response);
      return null; // Unreachable code, handleErrorResponse always throws
    }
  }

  /**
   * Handles error responses by throwing appropriate runtime exceptions.
   *
   * @param response the HttpResponse to handle
   */
  private void handleErrorResponse(HttpResponse<String> response) {
    int statusCode = response.statusCode();
    String responseBody = response.body();
    if (statusCode >= 400 && statusCode < 500) {
      throw new RuntimeException("Client error: " + statusCode + " - " + responseBody);
    } else if (statusCode >= 500) {
      throw new RuntimeException("Server error: " + statusCode + " - " + responseBody);
    } else {
      throw new RuntimeException("Unexpected error: " + statusCode + " - " + responseBody);
    }
  }

  /**
   * Sends a GET request to the specified endpoint with headers and parses the response.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param responseType the class of the response type
   * @param timeout      the timeout duration for the request
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T get(String endpoint, Map<String, String> headers, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.GET, endpoint, headers, null, timeout);
    return sendRequest(request, responseType);
  }

  /**
   * Sends a POST request to the specified endpoint with headers, body, and parses the response.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param body         the body of the request
   * @param responseType the class of the response type
   * @param timeout      the timeout duration for the request
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T post(String endpoint, Map<String, String> headers, String body, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.POST, endpoint, headers, body, timeout);
    return sendRequest(request, responseType);
  }

  /**
   * Sends a PUT request to the specified endpoint with headers, body, and parses the response.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param body         the body of the request
   * @param responseType the class of the response type
   * @param timeout      the timeout duration for the request
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T put(String endpoint, Map<String, String> headers, String body, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.PUT, endpoint, headers, body, timeout);
    return sendRequest(request, responseType);
  }

  /**
   * Sends a DELETE request to the specified endpoint with headers and parses the response.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param responseType the class of the response type
   * @param timeout      the timeout duration for the request
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T delete(String endpoint, Map<String, String> headers, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.DELETE, endpoint, headers, null, timeout);
    return sendRequest(request, responseType);
  }

  /**
   * Sends a GET request to the specified endpoint with headers and parses the response.
   * Uses a default timeout of 10 seconds.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param responseType the class of the response type
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T get(String endpoint, Map<String, String> headers, Class<T> responseType)
      throws IOException, InterruptedException {
    return get(endpoint, headers, responseType, Duration.ofSeconds(10));
  }

  /**
   * Sends a POST request to the specified endpoint with headers, body, and parses the response.
   * Uses a default timeout of 10 seconds.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param body         the body of the request
   * @param responseType the class of the response type
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T post(String endpoint, Map<String, String> headers, String body, Class<T> responseType)
      throws IOException, InterruptedException {
    return post(endpoint, headers, body, responseType, Duration.ofSeconds(10));
  }

  /**
   * Sends a PUT request to the specified endpoint with headers, body, and parses the response.
   * Uses a default timeout of 10 seconds.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param body         the body of the request
   * @param responseType the class of the response type
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T put(String endpoint, Map<String, String> headers, String body, Class<T> responseType)
      throws IOException, InterruptedException {
    return put(endpoint, headers, body, responseType, Duration.ofSeconds(10));
  }

  /**
   * Sends a DELETE request to the specified endpoint with headers and parses the response.
   * Uses a default timeout of 10 seconds.
   *
   * @param endpoint     the API endpoint
   * @param headers      the headers to include in the request
   * @param responseType the class of the response type
   * @param <T>          the type of the response
   * @return the parsed response of type T
   * @throws IOException          if an I/O error occurs
   * @throws InterruptedException if the operation is interrupted
   */
  public <T> T delete(String endpoint, Map<String, String> headers, Class<T> responseType)
      throws IOException, InterruptedException {
    return delete(endpoint, headers, responseType, Duration.ofSeconds(10));
  }
}
