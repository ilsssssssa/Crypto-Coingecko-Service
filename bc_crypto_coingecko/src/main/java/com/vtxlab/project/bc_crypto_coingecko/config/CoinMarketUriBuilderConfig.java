package com.vtxlab.project.bc_crypto_coingecko.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import com.vtxlab.project.bc_crypto_coingecko.infra.ApiUtil;
import com.vtxlab.project.bc_crypto_coingecko.infra.UriScheme;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CoinMarketUriBuilderConfig {

  @Value("${api.coingecko.key}")
  private String coingeckoKey;

  @Value("${api.coingecko.domain}")
  private String domain;

  @Value("${api.coingecko.path}")
  private String path;

  @Value("${api.coingecko.endpoint}")
  private String endpoint;

  @Value("${api.coingecko.coins.vs_currency}")
  private String coinsVsCurrency;

  @Value("${api.coingecko.coins.order}")
  private String coinsOrder;

  @Value("${api.coingecko.coins.per_page}")
  private String coinsPerPage;

  @Value("${api.coingecko.coins.page}")
  private String coinsPage;

  @Value("${api.coingecko.coins.sparkline}")
  private String coinsSparkline;

  @Value("${api.coingecko.coins.locale}")
  private String coinsLocale;

  @Bean
  UriComponentsBuilder coingeckoUriBuilder() {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("vs_currency", coinsVsCurrency);
    queryParams.add("coingeckoKey", coingeckoKey);
    // queryParams.add("order", coinsOrder);
    // queryParams.add("per_page", coinsPerPage);
    // queryParams.add("page", coinsPage);
    // queryParams.add("sparkline", coinsSparkline);
    // queryParams.add("locale", coinsLocale);
    log.info("coingeckoUriBuilder " + ApiUtil.uriBuilder(UriScheme.HTTPS,
        domain, path, endpoint, queryParams));
    return ApiUtil.uriBuilder(UriScheme.HTTPS, domain, path, endpoint,
        queryParams);
  }
}
