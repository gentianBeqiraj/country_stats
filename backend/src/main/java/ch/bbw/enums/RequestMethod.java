package ch.bbw.enums;

import lombok.Getter;

@Getter
public enum RequestMethod {

  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE");

  private final String value;

  RequestMethod(String value) {
    this.value = value;
  }
}
