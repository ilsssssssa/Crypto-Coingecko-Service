package com.vtxlab.project.bc_crypto_coingecko.annotation;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoinSymbol {

  @Value("${api.coingecko.coin-ids}")
  private String coinIds;

  public List<String> getCoinIds() {
    return Arrays.asList(coinIds.split(","));
  }
}
