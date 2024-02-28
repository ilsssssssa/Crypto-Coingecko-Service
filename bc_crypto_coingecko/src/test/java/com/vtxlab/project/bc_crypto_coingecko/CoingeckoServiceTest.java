package com.vtxlab.project.bc_crypto_coingecko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.vtxlab.project.bc_crypto_coingecko.config.RedisHelper;
import com.vtxlab.project.bc_crypto_coingecko.infra.Mapper;
import com.vtxlab.project.bc_crypto_coingecko.infra.TraditionCurrency;
import com.vtxlab.project.bc_crypto_coingecko.infra.UriScheme;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;
import com.vtxlab.project.bc_crypto_coingecko.service.CoingeckoService;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
public class CoingeckoServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisHelper redisUtils;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private CoingeckoService coingeckoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getDataFromApiTest() throws Exception {
        // java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@4cae66a8 testClass = com.vtxlab.project.bc_crypto_coingecko.CoingeckoServiceTest, locations =
        // [], classes = [com.vtxlab.project.bc_crypto_coingecko.BcCryptoCoingeckoApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties =
        String urlExpected =
                "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&locale=en";

        Coingecko mockCoingecko = Coingecko.builder()//
                .id("ethereum")//
                .symbol("eth")//
                .name("Ethereum")//
                .image("https://assets.coingecko.com/coins/images/279/large/ethereum.png?1696501628")//
                .currentPrice(2904.95)//
                .marketCap(349685582615L)//
                .marketCapRank(2)//
                .fullyDilutedValuation(349685582615L)//
                .totalVolume(15499073856L)//
                .high24h(2981.77)//
                .low24h(2885.03)//
                .priceChange24h(19.92)//
                .priceChangePercentage24h(0.69054)//
                .marketCapChange24h(2629862840L)//
                .marketCapChangePercentage24h(0.75776)//
                .circulatingSupply((long) 120164478.404969)//
                .totalSupply((long) 120164478.404969)//
                // .maxSupply(null)//
                .ath((long) 4878.26)//
                .athChangePercentage(-40.36347)//
                .athDate(LocalDateTime.of(2021, 11, 10, 14, 24, 19))//
                .atl(0.432979)//
                .atlChangePercentage(671809.28285)//
                .atlDate(LocalDateTime.of(2015, 10, 20, 0, 0, 0))//
                .roi(Coingecko.Roi.builder()//
                        .times(74.34929364119373)//
                        .currency("btc")//
                        .percentage(7434.929364119372)//
                        .build())//
                .lastUpdated(LocalDateTime.of(2024, 02, 20, 04, 06, 05))//
                .build(); //

        Coingecko[] coingecko = new Coingecko[] {mockCoingecko};
        List<Coingecko> coingeckoList = new ArrayList<>(List.of(mockCoingecko));
        // mock
        when(restTemplate.getForObject(urlExpected, Coingecko[].class))
                .thenReturn(coingecko);
        // Assertions
        assertEquals(urlExpected, UriComponentsBuilder.newInstance()//
                .scheme(UriScheme.HTTPS.name().toLowerCase())//
                .host("api.coingecko.com")//
                .path("/api/v3")//
                .path("/coins/markets")//
                .queryParam("vs_currency", TraditionCurrency.USD.name())//
                .queryParam("order", "market_cap_desc")//
                .queryParam("per_page", 100)//
                .queryParam("page", 1)//
                .queryParam("sparkline", false)//
                .queryParam("locale", "en")//
                .build()//
                .toUriString());//
        // Verify that restTemplate.getForObject() is called with the correct URL
        verify(restTemplate).getForObject(
                "https://api.coingecko.com/api/v3/simple/price?ids=btc,eth&vs_currencies=usd",
                Coingecko[].class);

        assertEquals(coingeckoList,
                Arrays.stream(coingecko).collect(Collectors.toList()));
        // Verify that mapper.map() is called for each Coingecko object
        verify(mapper).map(coingecko[0]);
        verify(mapper).map(coingecko[1]);

        // Verify that redisUtils.set() is called with the correct key and value
        verify(redisUtils).set("eth", coingecko[0]);
    }
}
