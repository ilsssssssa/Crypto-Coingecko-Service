package com.vtxlab.project.bc_crypto_coingecko.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.vtxlab.project.bc_crypto_coingecko.infra.Mapper;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;
import com.vtxlab.project.bc_crypto_coingecko.model.CoingeckoDTO;
import com.vtxlab.project.bc_crypto_coingecko.service.CoingeckoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class ScheduledConfig {

  @Autowired
  RedisHelper redisHelper;

  @Autowired
  CoingeckoService coingeckoService;

  @Autowired
  Mapper mapper;

  @Value("${redis-key.crypto.coingecko.coins-markets.currency}")
  String currency;

  @Value("${redis-key.crypto.coingecko.coins-markets.coin-ids}")
  String coinIds;

  private void setDataToRedis(List<CoingeckoDTO> entities) {
    entities.forEach(e -> {
      redisHelper.set(
          "crypto:coingecko:coins-markets:" + currency + ":" + e.getId(), e,
          60);
    });
  }

  // @Scheduled(fixedRate = 60000)
  public void scheduleFixedRateTask() throws Exception {
    // make System.currentTimeMillis() to seconds
    List<Coingecko> targetData = coingeckoService.getCoinMarket();

    List<String> validCoinList = Arrays.asList(coinIds.split(","));
    log.info("validCoinList: {}", validCoinList);
    validCoinList.stream()//
        .forEach(coinId -> {
          List<CoingeckoDTO> result = targetData.stream()//
              .filter(e -> validCoinList.contains(e.getSymbol()))
              .map(e -> mapper.map(e))//
              .collect(Collectors.toList());
          this.setDataToRedis(result);
        });

  }


  // @Scheduled(fixedDelay = 3000)
  public void scheduleFixedDelayTask() {
    System.out
        .println("Fixed delay task - " + System.currentTimeMillis() / 1000);
  }


  // @Scheduled(cron = "*/5 * * * * ?")
  @Async
  public void scheduleTaskUsingCronExpression() throws InterruptedException {
    System.out.println("Cron Task - " + System.currentTimeMillis() / 1000);
    // Thread.sleep(10000);
    System.out.println("End Cron ");
  }

  // @Scheduled(cron = "10 * * * * ?")
  @Async
  public void cronTask() throws InterruptedException {
    System.out.println("Cron Task - " + System.currentTimeMillis() / 1000);
    // Thread.sleep(10000);
    System.out.println("End Cron ");
  }


}
