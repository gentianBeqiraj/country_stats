package ch.bbw.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiInfoResponse {

  Boolean error;
  String msg;
  List<CountryInfoResponse> data = new ArrayList<>();
}
