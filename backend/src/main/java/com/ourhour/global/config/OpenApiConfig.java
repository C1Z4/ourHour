package com.ourhour.global.config;

import com.ourhour.global.exception.ErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

        @Value("${swagger.server.url:http://localhost:8080}")
        private String serverUrl;

        @Bean
        public OpenAPI openAPI() {
                String bearerAuthSchemeName = "bearerAuth";

                SecurityScheme bearerAuthScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization");

                SecurityRequirement globalSecurityRequirement = new SecurityRequirement()
                                .addList(bearerAuthSchemeName);

                return new OpenAPI()
                        .info(new Info()
                                .title("OurHour API")
                                .description("OurHour Backend API 문서")
                                .version("v1"))
                        .servers(Arrays.asList(
                                new Server().url(serverUrl).description("Backend Server")))
                        .components(new Components()
                                .addSecuritySchemes(bearerAuthSchemeName, bearerAuthScheme))
                        .addSecurityItem(globalSecurityRequirement);
        }

        // 모든 엔드포인트에 공통 에러 응답을 ErrorCode 기준으로 등록
        @Bean
        public OpenApiCustomizer globalErrorResponsesCustomizer() {
                return openApi -> {
                        // 에러 응답 스키마 정의
                        Components components = openApi.getComponents();
                        if (components == null) {
                                components = new Components();
                                openApi.setComponents(components);
                        }

                        Schema<?> errorSchema = new ObjectSchema()
                                        .addProperty("status", new StringSchema().example("BAD_REQUEST"))
                                        .addProperty("errorCode", new IntegerSchema().example(1000))
                                        .addProperty("message", new StringSchema().example("유효하지 않은 요청입니다"))
                                        .addProperty("data", new StringSchema().nullable(true));
                        components.addSchemas("ErrorResponse", errorSchema);

                        // ErrorCode를 HTTP 상태별로 그룹화
                        Map<HttpStatus, List<ErrorCode>> groupedByStatus = Arrays.stream(ErrorCode.values())
                                        .collect(Collectors.groupingBy(ErrorCode::getStatus));

                        // 공통(항상 노출) 에러코드 최소화
                        Set<ErrorCode> commonCodes = new HashSet<>(Arrays.asList(
                                        ErrorCode.INVALID_REQUEST,
                                        ErrorCode.UNAUTHORIZED,
                                        ErrorCode.FORBIDDEN,
                                        ErrorCode.INTERNAL_SERVER_ERROR));

                        // 태그명 기준 도메인 그룹 매핑
                        Map<String, Set<Integer>> tagToGroups = new HashMap<>();
                        tagToGroups.put("인증", setOf(1));
                        tagToGroups.put("이메일 인증", setOf(1));
                        tagToGroups.put("비밀번호 재설정", setOf(1));
                        tagToGroups.put("사용자", setOf(1));
                        tagToGroups.put("멤버", setOf(2));
                        tagToGroups.put("조직", setOf(3));
                        tagToGroups.put("조직 구성원", setOf(3, 2));
                        tagToGroups.put("프로젝트", setOf(4));
                        tagToGroups.put("게시판", setOf(5));
                        tagToGroups.put("게시글", setOf(5));
                        tagToGroups.put("댓글", setOf(6));
                        tagToGroups.put("채팅(REST)", setOf(7));
                        tagToGroups.put("깃허브 연동", setOf(9));

                        // 경로 힌트 기반 매핑 (태그가 없을 때 보조)
                        Map<String, Set<Integer>> pathHintToGroups = new HashMap<>();
                        pathHintToGroups.put("/api/auth", setOf(1));
                        pathHintToGroups.put("/api/user", setOf(1));
                        pathHintToGroups.put("/api/members", setOf(2));
                        pathHintToGroups.put("/api/organizations", setOf(3));
                        pathHintToGroups.put("/api/projects", setOf(4));
                        pathHintToGroups.put("/boards", setOf(5));
                        pathHintToGroups.put("/posts", setOf(5));
                        pathHintToGroups.put("/api/comments", setOf(6));
                        pathHintToGroups.put("/chat-rooms", setOf(7));
                        pathHintToGroups.put("/api/github", setOf(9));

                        // 각 오퍼레이션에 상태코드별 공통 에러 응답 추가
                        if (openApi.getPaths() == null) {
                                return;
                        }

                        openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperations().forEach(operation -> {
                                Set<Integer> allowedGroups = new HashSet<>();
                                if (operation.getTags() != null && !operation.getTags().isEmpty()) {
                                        operation.getTags().forEach(tag -> {
                                                Set<Integer> groups = tagToGroups.get(tag);
                                                if (groups != null) {
                                                        allowedGroups.addAll(groups);
                                                }
                                        });
                                }
                                if (allowedGroups.isEmpty()) {
                                        pathHintToGroups.forEach((hint, groups) -> {
                                                if (path.contains(hint)) {
                                                        allowedGroups.addAll(groups);
                                                }
                                        });
                                }

                                groupedByStatus.forEach((httpStatus, codes) -> {
                                        if (httpStatus == HttpStatus.OK || httpStatus.is1xxInformational()
                                                        || httpStatus.is2xxSuccessful()) {
                                                return;
                                        }

                                        List<ErrorCode> filtered = codes.stream()
                                                        .filter(ec -> commonCodes.contains(ec)
                                                                        || allowedGroups.contains(
                                                                                        ec.getStatusCode() / 1000))
                                                        .collect(Collectors.toList());

                                        if (filtered.isEmpty()) {
                                                return;
                                        }

                                        String statusCode = Integer.toString(httpStatus.value());
                                        String description = buildDescription(httpStatus, filtered);

                                        ApiResponse apiResponse = new ApiResponse()
                                                        .description(description)
                                                        .content(new Content().addMediaType(
                                                                        "application/json",
                                                                        new MediaType().schema(new Schema<>().$ref(
                                                                                        "#/components/schemas/ErrorResponse"))));

                                        operation.getResponses().addApiResponse(statusCode, apiResponse);
                                });
                        }));
                };
        }

        private String buildDescription(HttpStatus httpStatus, List<ErrorCode> codes) {
                String header = String.format("%s 응답. 가능한 errorCode:", httpStatus.name());
                String joined = codes.stream()
                                .map(ec -> String.format("- %d %s: %s", ec.getStatusCode(), ec.name(), ec.getMessage()))
                                .collect(Collectors.joining("\n"));
                return header + "\n" + joined;
        }

        private static Set<Integer> setOf(Integer... values) {
                return new HashSet<>(Arrays.asList(values));
        }
}
