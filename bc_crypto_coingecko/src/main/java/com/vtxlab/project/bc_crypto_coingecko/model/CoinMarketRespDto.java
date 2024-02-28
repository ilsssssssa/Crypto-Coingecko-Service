package com.vtxlab.project.bc_crypto_coingecko.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // always
@Setter // for modelmapper
@AllArgsConstructor // Testing purpose
@NoArgsConstructor // important for modelmapper
@Builder // Nice to have
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CoinMarketRespDto {
  private String id;
  private String symbol;
  private String name;
  private String image;
  private BigInteger totalVolume;
  private BigDecimal currentPrice;
  private Long marketCap;
  private Integer marketCapRank;
  @JsonProperty("price_change_percentage_24h")
  private BigDecimal priceChangePercentage24h;
}
