package com.vtxlab.project.bc_crypto_coingecko.exception;

import java.util.List;
import java.util.Objects;
import org.springframework.validation.BindingResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum.Syscode;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResp<T> {
  private String syscode;
  private String message;
  private T data;

  public String getSyscode() {
    return this.syscode;
  }

  public String getMessage() {
    return this.message;
  }

  public T getData() {
    return this.data;
  }

  public static <T> ApiResponseBuilder<T> builder() {
    return new ApiResponseBuilder<>();
  }

  private ApiResp(ApiResponseBuilder<T> builder) {
    this.syscode = builder.syscode;
    this.message = builder.message;
    this.data = builder.data;
  }

  public static class ApiResponseBuilder<T> {
    private String syscode;
    private String message;
    private T data;

    public ApiResponseBuilder<T> status(Syscode syscode) {
      this.syscode = syscode.getSyscode();
      this.message = syscode.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> concatMessageIfPresent(String str) {
      if (this.message != null && str != null)
        this.message += " " + str;
      return this;
    }

    public ApiResponseBuilder<T> ok() {
      this.syscode = Syscode.OK.getSyscode();
      this.message = Syscode.OK.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> error() {
      this.syscode = Syscode.INVALID_INPUT.getSyscode();
      this.message = Syscode.INVALID_INPUT.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> error(Syscode syscode) {
      this.syscode = syscode.getSyscode();
      return this;
    }

    public ApiResponseBuilder<T> error(Syscode syscode, BindingResult bindingResult) {
      this.syscode = syscode.getSyscode();
      this.message =
          bindingResult.getFieldError() != null
              ? Objects.requireNonNull(bindingResult.getFieldError()).getField()
                  + " " + bindingResult.getFieldError().getCode()
              : null;
      return this;
    }

    public ApiResponseBuilder<T> Syscode(String syscode) {
      this.syscode = syscode;
      return this;
    }

    public ApiResponseBuilder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponseBuilder<T> message(String message) {
      this.message = message;
      return this;
    }

    public ApiResp<T> build() {
      if (this.syscode.isEmpty() || this.message == null)
        throw new RuntimeException();
      return new ApiResp<>(this);
    }


  }
}

// {
// "Syscode" : 200,
// "message" : "OK",
// "data" : [

// ],
// "error" : [
// "", ""
// ],
// }
