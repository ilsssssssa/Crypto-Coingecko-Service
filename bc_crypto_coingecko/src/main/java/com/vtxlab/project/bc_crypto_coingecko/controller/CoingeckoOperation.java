package com.vtxlab.project.bc_crypto_coingecko.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.vtxlab.project.bc_crypto_coingecko.annotation.SymbolCheck;
import com.vtxlab.project.bc_crypto_coingecko.exception.ApiResp;
import com.vtxlab.project.bc_crypto_coingecko.model.CoinMarketRespDto;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;

@Validated
public interface CoingeckoOperation {

  @GetMapping("/market")
  @ResponseStatus(HttpStatus.OK)
  public List<CoinMarketRespDto> getMarketData();

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  public ApiResp<List<Coingecko>> getAllData(@RequestParam String currency,
      @SymbolCheck @RequestParam String ids);

  @GetMapping("/coinList")
  @ResponseStatus(HttpStatus.OK)
  public ApiResp<List<String>> getCoinList();
}
