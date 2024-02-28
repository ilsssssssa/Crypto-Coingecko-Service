package com.vtxlab.project.bc_crypto_coingecko.infra;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiUtil {

  public static UriComponentsBuilder uriBuilder(UriScheme uriScheme,
      String domain, String path, String endpoint,
      MultiValueMap<String, String> queryParams, String... pathSegments) {
    return UriComponentsBuilder.newInstance()//
        .scheme(uriScheme.getProtocol())//
        .host(domain)//
        .pathSegment(pathSegments)//
        .path(path)//
        .path(endpoint)//
        .queryParams(queryParams);
  }
}
