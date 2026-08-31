package com.cryptalk;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SocialApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void supportsRichPostAndSocialInteractions() throws Exception {
        Account author = signup("social-author@example.com", "작성자");
        Account reader = signup("social-reader@example.com", "독자");

        MockMultipartFile image = new MockMultipartFile("file", "chart.png", "image/png", new byte[]{1, 2, 3});
        MvcResult upload = mvc.perform(multipart("/api/v1/media").file(image).header("Authorization", bearer(author.token())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mediaType").value("IMAGE"))
            .andReturn();
        String mediaUrl = json.readTree(upload.getResponse().getContentAsString()).get("url").asText();

        String createBody = """
            {
              "coinSymbol":"ETH",
              "title":"ETH 차트 분석",
              "content":"지지 구간을 확인했습니다.",
              "media":[{"type":"IMAGE","url":"%s"}],
              "tradingViewSymbol":"BINANCE:ETHUSDT",
              "tradingViewInterval":"60",
              "tradingViewAnalysis":"1시간봉 지지선 관찰",
              "assetPrice":4321.25,
              "assetPriceCurrency":"USDT",
              "youtubeUrl":"https://www.youtube.com/shorts/dQw4w9WgXcQ"
            }
            """.formatted(mediaUrl);
        MvcResult created = mvc.perform(post("/api/v1/posts")
                .header("Authorization", bearer(author.token()))
                .contentType(MediaType.APPLICATION_JSON).content(createBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.media[0].type").value("IMAGE"))
            .andExpect(jsonPath("$.tradingView.symbol").value("BINANCE:ETHUSDT"))
            .andExpect(jsonPath("$.priceSnapshot.capturedAt").isNotEmpty())
            .andExpect(jsonPath("$.youtube.videoId").value("dQw4w9WgXcQ"))
            .andExpect(jsonPath("$.youtube.thumbnailUrl").value("https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg"))
            .andReturn();
        long postId = json.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(post("/api/v1/posts/{postId}/likes", postId).header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.likes").value(1)).andExpect(jsonPath("$.liked").value(true));
        mvc.perform(post("/api/v1/posts/{postId}/comments", postId).header("Authorization", bearer(reader.token()))
                .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"좋은 분석이에요\"}"))
            .andExpect(status().isOk());
        mvc.perform(post("/api/v1/posts/{postId}/bookmarks", postId).header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.bookmarked").value(true));
        mvc.perform(post("/api/v1/posts/{postId}/reposts", postId).header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.reposted").value(true)).andExpect(jsonPath("$.reposts").value(1));
        mvc.perform(post("/api/v1/members/{memberId}/follow", author.id()).header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.followers").value(1)).andExpect(jsonPath("$.followedByMe").value(true));

        mvc.perform(get("/api/v1/feed").header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(postId))
            .andExpect(jsonPath("$[0].comments").value(1)).andExpect(jsonPath("$[0].bookmarked").value(true));
        mvc.perform(get("/api/v1/me/bookmarks").header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(postId));

        mvc.perform(delete("/api/v1/posts/{postId}/reposts", postId).header("Authorization", bearer(reader.token())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.reposted").value(false));
    }

    private Account signup(String email, String nickname) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"strong-password-123\",\"nickname\":\"" + nickname + "\"}";
        MvcResult result = mvc.perform(post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk()).andReturn();
        JsonNode response = json.readTree(result.getResponse().getContentAsString());
        return new Account(response.get("accessToken").asText(), response.get("member").get("id").asLong());
    }

    private String bearer(String token) { return "Bearer " + token; }
    private record Account(String token, long id) {}
}
