package com.vtxlab.project.bc_crypto_coingecko.infra;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;
import com.vtxlab.project.bc_crypto_coingecko.model.CoingeckoDTO;

@Component
public class Mapper {

  @Autowired
  ModelMapper modelMapper;

  public CoingeckoDTO map(Coingecko coingecko) {
    return CoingeckoDTO.builder()//
        .id(coingecko.getId())//
        .symbol(coingecko.getSymbol())//
        .name(coingecko.getName())//
        .currentPrice(coingecko.getCurrentPrice())//
        .marketCap(coingecko.getMarketCap())//
        .high24h(coingecko.getHigh24h())//
        .low24h(coingecko.getLow24h())//
        .priceChange24h(coingecko.getPriceChange24h())//
        .build();
  }
}
