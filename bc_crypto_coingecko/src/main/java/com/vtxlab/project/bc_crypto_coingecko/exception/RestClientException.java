package com.vtxlab.project.bc_crypto_coingecko.exception;

import com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum.Syscode;

import lombok.Getter;

@Getter
public class RestClientException extends BusinessException {
  private Syscode Syscode;

  public RestClientException(Syscode Syscode) {
    super(Syscode);
    this.Syscode = Syscode;
  }
}
