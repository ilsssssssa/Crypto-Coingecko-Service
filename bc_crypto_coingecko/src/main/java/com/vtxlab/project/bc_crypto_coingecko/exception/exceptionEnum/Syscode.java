package com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum;

import lombok.Getter;

@Getter
public enum Syscode {
  OK("000000", "OK"), //
  INVALID_INPUT("9", "Invalid input"), //
  INVALID_OPERATION("10", "Invalid operation"), //
  // Api error
  API_ERROR("100", "API error"), //
  COINGECKO_SERVICE_UNAVAILABLE("900000", "Coingecko service is unavailable");
  //

  private String syscode;
  private String message;

  private Syscode(String syscode, String message) {
    this.syscode = syscode;
    this.message = message;
  }

  public static Syscode fromCode(String syscode) {
    for (Syscode c : Syscode.values()) {
      if (c.getSyscode().equals(syscode)) {
        return c;
      }
    }
    return null;
  }

}
