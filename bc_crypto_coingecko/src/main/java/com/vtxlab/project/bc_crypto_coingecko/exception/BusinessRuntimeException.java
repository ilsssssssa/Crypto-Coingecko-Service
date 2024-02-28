package com.vtxlab.project.bc_crypto_coingecko.exception;

import com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum.Syscode;
import lombok.Getter;

@Getter
public class BusinessRuntimeException extends RuntimeException {
  private Syscode Syscode;

  public BusinessRuntimeException(Syscode Syscode) {
    super(Syscode.getMessage());
    this.Syscode = Syscode;
  }

}
