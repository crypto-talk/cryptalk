package com.cryptalk.config;

import com.cryptalk.common.ErrorResponse;
import com.cryptalk.common.ErrorCode;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI cryptalkOpenApi() {
        Components components = new Components()
            .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));
        return new OpenAPI()
            .info(new Info()
                .title("CrypTalk API")
                .description("코인 커뮤니티 CrypTalk 백엔드 REST API")
                .version("v1"))
            .components(components)
            .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    OpenApiCustomizer commonErrorResponseCustomizer() {
        Schema<Object> schema = new Schema<>();
        schema.set$ref("#/components/schemas/ErrorResponse");
        ApiResponse errorResponse = new ApiResponse()
            .description("공통 오류 응답. code는 클라이언트 분기용 고정 계약입니다.")
            .content(new Content().addMediaType("application/json", new MediaType().schema(schema)));
        return openApi -> {
            ModelConverters.getInstance().read(ErrorResponse.class)
                .forEach(openApi.getComponents()::addSchemas);
            openApi.getComponents().addSchemas("ErrorCode", new StringSchema()
                .description("클라이언트가 분기에 사용하는 고정 오류 코드")
                ._enum(java.util.Arrays.stream(ErrorCode.values()).map(Enum::name).toList()));
            openApi.getPaths().values().forEach(path -> path.readOperations().forEach(operation ->
                operation.getResponses().addApiResponse("default", errorResponse)));
        };
    }
}
