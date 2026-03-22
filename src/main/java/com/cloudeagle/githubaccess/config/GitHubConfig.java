package com.cloudeagle.githubaccess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GitHubConfig {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.thread-pool.size:10}")
    private int threadPoolSize;

    @Bean
    public RestTemplate githubRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("X-GitHub-Api-Version", "2022-11-28");
            return execution.execute(request, body);
        };
        restTemplate.setInterceptors(List.of(authInterceptor));
        return restTemplate;
    }

    @Bean
    public ExecutorService githubExecutorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
