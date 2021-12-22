package com.optimagrowth.licensingservice.model.utils;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;

public class RestErrorList extends ArrayList<ErrorMessage> {

  private static final long serialVersionUID = 1L;

  private HttpStatus status;

  public RestErrorList(HttpStatus status, ErrorMessage... errors) {
    this(status.value(), errors);
  }

  public RestErrorList(int status, ErrorMessage... errors) {
    super();
    this.status = HttpStatus.valueOf(status);
    addAll(asList(errors));
  }

  /** @return the status */
  public HttpStatus getStatus() {
    return status;
  }

  /** @param status the status to set */
  public void setStatus(HttpStatus status) {
    this.status = status;
  }
}
