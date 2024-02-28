package com.vtxlab.project.bc_crypto_coingecko.exception;

import com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum.Syscode;
import lombok.Getter;

@Getter
public class BusinessException extends Exception {
  private Syscode Syscode;

  public BusinessException(Syscode Syscode) {
    super(Syscode.getMessage());
    this.Syscode = Syscode;
  }
}
