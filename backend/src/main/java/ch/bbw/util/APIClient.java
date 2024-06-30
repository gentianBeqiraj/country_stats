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

public class APIClient {
  private final String baseUrl;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public APIClient(String baseUrl, Integer timeout) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(timeout))
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build();
    this.objectMapper = new ObjectMapper();
  }

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

  public <T> T get(String endpoint, Map<String, String> headers, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.GET, endpoint, headers, null, timeout);
    return sendRequest(request, responseType);
  }

  public <T> T post(String endpoint, Map<String, String> headers, String body, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.POST, endpoint, headers, body, timeout);
    return sendRequest(request, responseType);
  }

  public <T> T put(String endpoint, Map<String, String> headers, String body, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.PUT, endpoint, headers, body, timeout);
    return sendRequest(request, responseType);
  }

  public <T> T delete(String endpoint, Map<String, String> headers, Class<T> responseType, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = buildRequest(Method.DELETE, endpoint, headers, null, timeout);
    return sendRequest(request, responseType);
  }

  public <T> T get(String endpoint, Map<String, String> headers, Class<T> responseType)
      throws IOException, InterruptedException {
    return get(endpoint, headers, responseType, Duration.ofSeconds(10));
  }

  public <T> T post(String endpoint, Map<String, String> headers, String body, Class<T> responseType)
      throws IOException, InterruptedException {
    return post(endpoint, headers, body, responseType, Duration.ofSeconds(10));
  }

  public <T> T put(String endpoint, Map<String, String> headers, String body, Class<T> responseType)
      throws IOException, InterruptedException {
    return put(endpoint, headers, body, responseType, Duration.ofSeconds(10));
  }

  public <T> T delete(String endpoint, Map<String, String> headers, Class<T> responseType)
      throws IOException, InterruptedException {
    return delete(endpoint, headers, responseType, Duration.ofSeconds(10));
  }
}
