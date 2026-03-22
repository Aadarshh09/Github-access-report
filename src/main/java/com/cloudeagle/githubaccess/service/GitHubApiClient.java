package com.cloudeagle.githubaccess.service;

import com.cloudeagle.githubaccess.exception.GitHubApiException;
import com.cloudeagle.githubaccess.model.GitHubCollaborator;
import com.cloudeagle.githubaccess.model.GitHubRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Low-level GitHub API client.
 * Handles authentication, pagination, and error translation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubApiClient {
    public GitHubApiClient(RestTemplate githubRestTemplate) {
        this.githubRestTemplate = githubRestTemplate;
    }

    private final RestTemplate githubRestTemplate;

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.per-page:100}")
    private int perPage;

    /**
     * Fetch all repositories for a given organization.
     * Handles pagination automatically — supports 100+ repos.
     */
    public List<GitHubRepo> fetchAllRepos(String org) {
        System.out.println("Fetching repositories for org: " + org);
        String url = baseUrl + "/orgs/{org}/repos?per_page={perPage}&page={page}&type=all";
        return fetchAllPages(url, new ParameterizedTypeReference<List<GitHubRepo>>() {}, org);
    }

    /**
     * Fetch all collaborators for a specific repository.
     * Handles pagination automatically — supports 1000+ users.
     */
    public List<GitHubCollaborator> fetchRepoCollaborators(String org, String repoName) {
        System.out.println("Fetching collaborators for repo: " + org + "/" + repoName);
        String url = baseUrl + "/repos/{org}/{repo}/collaborators?per_page={perPage}&page={page}&affiliation=all";
        return fetchAllPagesForRepo(url, new ParameterizedTypeReference<List<GitHubCollaborator>>() {}, org, repoName);
    }

    /**
     * Generic paginator for org-level endpoints.
     */
    private <T> List<T> fetchAllPages(
            String urlTemplate,
            ParameterizedTypeReference<List<T>> responseType,
            String org) {

        List<T> allItems = new ArrayList<>();
        int page = 1;

        while (true) {
            try {
                ResponseEntity<List<T>> response = githubRestTemplate.exchange(
                        urlTemplate, HttpMethod.GET, null, responseType, org, perPage, page
                );

                List<T> body = response.getBody();
                if (body == null || body.isEmpty()) break;

                allItems.addAll(body);
                System.out.println("Fetched page " + page + " for org " + org + " got " + body.size() + " items");

                if (body.size() < perPage) break; // Last page
                page++;

            } catch (HttpClientErrorException ex) {
                throw new GitHubApiException(
                        "GitHub API error for org " + org + ": " + ex.getMessage(),
                        ex.getStatusCode().value()
                );
            }
        }

        return allItems;
    }

    /**
     * Generic paginator for repo-level endpoints.
     */
    private <T> List<T> fetchAllPagesForRepo(
            String urlTemplate,
            ParameterizedTypeReference<List<T>> responseType,
            String org,
            String repo) {

        List<T> allItems = new ArrayList<>();
        int page = 1;

        while (true) {
            try {
                ResponseEntity<List<T>> response = githubRestTemplate.exchange(
                        urlTemplate, HttpMethod.GET, null, responseType, org, repo, perPage, page
                );

                List<T> body = response.getBody();
                if (body == null || body.isEmpty()) break;

                allItems.addAll(body);

                if (body.size() < perPage) break;
                page++;

            } catch (HttpClientErrorException ex) {
                // 403 on a specific repo is common (e.g. insufficient scope) — log and skip
                if (ex.getStatusCode().value() == 403) {
                    System.out.println("Insufficient permissions for: " + org + "/" + repo);
                    break;
                }
                throw new GitHubApiException(
                        "GitHub API error for repo " + org + "/" + repo + ": " + ex.getMessage(),
                        ex.getStatusCode().value()
                );
            }
        }

        return allItems;
    }
}
