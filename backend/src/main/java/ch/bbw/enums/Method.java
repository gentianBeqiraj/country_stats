package ch.bbw.enums;

import lombok.Getter;

@Getter
public enum Method {

  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE");

  private final String value;

  Method(String value) {
    this.value = value;
  }
}
