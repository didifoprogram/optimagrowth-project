package com.optimagrowth.licensingservice.model.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class ResponseWrapper {

  private Object data;
  private Object metadata;
  private List<ErrorMessage> errors;

  public ResponseWrapper(Object data, Object metadata,
      List<ErrorMessage> errors) {
    this.data = data;
    this.metadata = metadata;
    this.errors = errors;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public Object getMetadata() {
    return metadata;
  }

  public void setMetadata(Object metadata) {
    this.metadata = metadata;
  }

  public List<ErrorMessage> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorMessage> errors) {
    this.errors = errors;
  }
}
