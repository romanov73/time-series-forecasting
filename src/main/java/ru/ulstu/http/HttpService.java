package ru.ulstu.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.ulstu.method.fuzzy.FuzzyRuleDataDto;
import ru.ulstu.method.fuzzy.OutputValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class HttpService {
    private final WebClient client = getWebClient();
    private final static String USER_NAME = "admin";
    private final static String PASSWORD = "admin";

    public WebClient getWebClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                        Base64.getEncoder().encodeToString((USER_NAME + ":" + PASSWORD).getBytes()))
                .build();
    }

    public List<OutputValue> post(String url, FuzzyRuleDataDto requestBody) {
        List<OutputValue> result = new ArrayList<>();

        try {
            OutputValue[] res = client
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(requestBody), FuzzyRuleDataDto.class)
                    .retrieve()
                    .bodyToMono(OutputValue[].class)
                    .block();
            if (res != null && res.length > 0) {
                result = Arrays.stream(res).toList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
