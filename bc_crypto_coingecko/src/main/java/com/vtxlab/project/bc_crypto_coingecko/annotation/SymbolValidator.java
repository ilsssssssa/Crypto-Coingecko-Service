package com.vtxlab.project.bc_crypto_coingecko.annotation;

import java.util.Objects;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SymbolValidator
    implements ConstraintValidator<SymbolCheck, CoinSymbol> {

  @Override
  public boolean isValid(CoinSymbol value, ConstraintValidatorContext context) {
    try {
      return Objects.nonNull(value) && value.getCoinIds().contains(value);
    } catch (Exception e) {
      return false;
    }
  }
}
