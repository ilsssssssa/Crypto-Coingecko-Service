package com.vtxlab.project.bc_crypto_coingecko.service;

import java.util.List;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;

public interface CoingeckoService {
  List<Coingecko> getCoinMarket();

  List<Coingecko> getDataFromApi(String currency, String ids);

  List<String> getCoinList();

}
