package com.cryptalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cryptalk.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.liquibase.contexts=mock")
@AutoConfigureMockMvc
class MockSeedApiTest {
    @Autowired MockMvc mvc;
    @Autowired PostRepository posts;
    @Autowired JdbcTemplate jdbc;

    @Test
    void exposesMockPostsWithHolderSnapshotsAndEngagement() throws Exception {
        assertEquals(30, posts.count());
        assertEquals(20, jdbc.queryForObject("SELECT COUNT(DISTINCT coin_id) FROM posts", Integer.class));

        mvc.perform(get("/api/v1/communities/ETH/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5))
            .andExpect(jsonPath("$[0].title").value("이더리움 장기 보유자가 보는 이번 분기 핵심"))
            .andExpect(jsonPath("$[0].verifiedHolder").value(false))
            .andExpect(jsonPath("$[0].holderSnapshot.verificationAvailability").value("SUPPORTED"))
            .andExpect(jsonPath("$[0].holderSnapshot.verificationLevel").value("UNVERIFIED"))
            .andExpect(jsonPath("$[0].comments").value(1))
            .andExpect(jsonPath("$[1].holderSnapshot.verificationLevel").value("WALLET"))
            .andExpect(jsonPath("$[1].holderSnapshot.quantityBand").value("10~100 ETH"))
            .andExpect(jsonPath("$[2].likes").value(3))
            .andExpect(jsonPath("$[2].comments").value(2))
            .andExpect(jsonPath("$[4].holderSnapshot.quantityBand").value("0.1~1 ETH"));

        mvc.perform(get("/api/v1/communities/BTC/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].holderSnapshot.verificationAvailability").value("NOT_SUPPORTED"))
            .andExpect(jsonPath("$[0].holderSnapshot.verificationLevel").doesNotExist());
    }

    @Test
    void includesMockPostsAndRepostsInFeed() throws Exception {
        mvc.perform(get("/api/v1/feed").param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(20))
            .andExpect(jsonPath("$.items[0].eventType").value("POST"))
            .andExpect(jsonPath("$.items[0].post.title").value("비트코인 단기 과열을 판단하는 세 가지 기준"))
            .andExpect(jsonPath("$.hasMore").value(true));

        mvc.perform(get("/api/v1/coins"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(20));
    }
}
