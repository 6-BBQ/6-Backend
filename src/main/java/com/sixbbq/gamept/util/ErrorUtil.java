package com.sixbbq.gamept.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.util.discord.dto.DiscordMessageDTO;
import com.sixbbq.gamept.util.discord.properties.DiscordProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class ErrorUtil {
    private static final int MAX_MESSAGE = 2000; // 메세지의 최대 글자수

    @Setter
    private static DiscordProperties discordProperties;

    /**
     *
     * @param exception : 에러 메세지
     * @param userId : 에러가 발생한 유저(디스코드 닉네임으로 출력됌)
     * @throws Exception : 처리 과정에서 에러가 발생하면 던져질 Exception
     */
    public static void logError(Exception exception, HttpServletRequest servletRequest, String userId) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        String description = "";
        description = description + "**에러 메세지** : " + exception.getMessage();
        description = description + "\n**발생한 예외명** : " + exception.getClass().getName();
        description = description + "\n**발생시간** : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        description = description + "\n**요청 URL** :" + createRequestFullPath(servletRequest);
        String body = getRequestBody(servletRequest);
        if(!body.isEmpty())
            description = description + "\n**요청Body값** : " + body;

        description = description + "\n**에러 메세지**\n```";

        int messageLength = MAX_MESSAGE - description.length();
        log.info(String.valueOf(messageLength));
        String shortStackTrace = stackTrace.length() > messageLength ?
                stackTrace.substring(0, DivideUtil.floorToTens(messageLength) - 10) : stackTrace;

        String content = discordProperties.getAdminId();

        description = description + shortStackTrace + "```";
        log.info(description);
        log.info(String.valueOf(description.length()));

        ObjectMapper objectMapper = new ObjectMapper();
        DiscordMessageDTO dto = DiscordMessageDTO.builder()
                .username(userId)
                .content(content)
                .embeds(List.of(DiscordMessageDTO.Embed.builder()
                                .title("에러정보")
                                .description(description)
                        .build()))
                .build();
        try {
            String json = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(discordProperties.getDiscordUrl()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    //요청타입(GET,POST,PUT 등)과 URL 전체경로 반환
    public static String createRequestFullPath(HttpServletRequest servletRequest) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(servletRequest);
        WebRequest webRequest = servletWebRequest;
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }

    // body에 담긴 값 가져오기
    private static String getRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        try {
            ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
            byte[] buf = wrapper.getContentAsByteArray();
            sb.append(new String(buf, wrapper.getCharacterEncoding()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return sb.toString();
    }
}
