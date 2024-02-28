package com.vtxlab.project.bc_crypto_coingecko;

// Remove the unused import statement
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.vtxlab.project.bc_crypto_coingecko.controller.impl.CoingeckoController;
import com.vtxlab.project.bc_crypto_coingecko.exception.ApiResp;
import com.vtxlab.project.bc_crypto_coingecko.exception.exceptionEnum.Syscode;
import com.vtxlab.project.bc_crypto_coingecko.model.Coingecko;
import com.vtxlab.project.bc_crypto_coingecko.service.CoingeckoService;

@WebMvcTest(CoingeckoController.class)
public class CoingeckoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @InjectMocks
  private CoingeckoController controller;

  @Mock
  private CoingeckoService coingeckoService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllData() throws Exception {
    // Mock data
    Coingecko btc = Coingecko.builder().id("bitcoin").symbol("btc")
        .name("Bitcoin").currentPrice(50000).build();
    Coingecko eth = Coingecko.builder().id("ethereum").symbol("eth")
        .name("Ethereum").currentPrice(3000).build();
    List<Coingecko> testData = new ArrayList<>(List.of(btc, eth));
    // Mock service response
    when(coingeckoService.getDataFromApi("usd", "btc,eth"))
        .thenReturn(testData);
    // Call the controller method
    mockMvc.perform(get("/crypto/coingecko/api/v1?currency=usd&ids=btc,eth"))//
        .andExpect(status().isOk())//
        .andExpect(jsonPath("$.syscode").value(Syscode.OK.getSyscode()))//
        .andExpect(jsonPath("$.message").value(Syscode.OK.getMessage()))//
        .andExpect(jsonPath("$.data").isArray())//
        .andExpect(jsonPath("$.data[0].id").value("bitcoin"))//
        .andExpect(jsonPath("$.data[0].symbol").value("btc"))//
        .andExpect(jsonPath("$.data[0].name").value("Bitcoin"))//
        .andExpect(jsonPath("$.data[0].currentPrice").value(50000))//
        .andExpect(jsonPath("$.data[1].id").value("ethereum"))//
        .andExpect(jsonPath("$.data[1].symbol").value("eth"))//
        .andExpect(jsonPath("$.data[1].name").value("Ethereum"))//
        .andExpect(jsonPath("$.data[1].currentPrice").value(3000));

    // Call the controller method

    ApiResp<List<Coingecko>> response = controller.getAllData("usd", "btc,eth");
    ResponseEntity<ApiResp<List<Coingecko>>> responseEntity =
        ResponseEntity.ok(response);
    // verify the response
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(Syscode.OK.getSyscode(), response.getSyscode());
    assertEquals(Syscode.OK.getMessage(), response.getMessage());
    assertEquals(testData, response.getData());

  }
}
