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
    void rejectsRemovedWalletLoginEndpoint() throws Exception {
        mvc.perform(post("/api/v1/auth/nonce")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"walletAddress\":\"0x1111111111111111111111111111111111111111\"}"))
            .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/v1/auth/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void signsUpAndLogsInWithLoginId() throws Exception {
        mvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"satoshi_21\",\"password\":\"strong-password-123\",\"nickname\":\"새회원\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.member.nickname").value("새회원"))
            .andExpect(jsonPath("$.member.walletAddress").doesNotExist());

        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"SATOSHI_21\",\"password\":\"strong-password-123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.member.nickname").value("새회원"));
    }

    @Test
    void rejectsInvalidLoginIdPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"missing-user\",\"password\":\"wrong-password\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsEmailOnlyLoginRequest() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"member@example.com\",\"password\":\"strong-password-123\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDuplicateLoginIdIgnoringCase() throws Exception {
        mvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"duplicate-user\",\"password\":\"strong-password-123\",\"nickname\":\"첫회원\"}"))
            .andExpect(status().isOk());

        mvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"DUPLICATE-USER\",\"password\":\"strong-password-123\",\"nickname\":\"둘째회원\"}"))
            .andExpect(status().isConflict());
    }
}
