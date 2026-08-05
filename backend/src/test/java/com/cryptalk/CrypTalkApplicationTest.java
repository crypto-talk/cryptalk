package com.cryptalk;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CrypTalkApplicationTest {
    @Autowired MockMvc mvc;

    @Test
    void exposesSeededCoins() throws Exception {
        mvc.perform(get("/api/v1/coins"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].symbol").value("BTC"))
            .andExpect(jsonPath("$[1].symbol").value("ETH"));
    }

    @Test
    void createsWalletLoginNonce() throws Exception {
        mvc.perform(post("/api/v1/auth/nonce")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"walletAddress\":\"0x1111111111111111111111111111111111111111\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nonceId").isNotEmpty())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("CrypTalk 로그인")));
    }
}
