package com.cloudeagle.githubaccess.service;

import com.cloudeagle.githubaccess.model.AccessReport;
import com.cloudeagle.githubaccess.model.AccessReport.RepoPermissions;
import com.cloudeagle.githubaccess.model.AccessReport.UserRepoAccess;
import com.cloudeagle.githubaccess.model.GitHubCollaborator;
import com.cloudeagle.githubaccess.model.GitHubRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Core service that orchestrates fetching repos + collaborators
 * and builds the aggregated user → repo access report.
 *
 * Scale strategy:
 *  - Fetch all repos first (paginated)
 *  - Fan out collaborator fetches in parallel using a fixed thread pool
 *  - Aggregate results into a user-keyed map
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessReportService {
    public AccessReportService(GitHubApiClient apiClient, ExecutorService githubExecutorService) {
        this.apiClient = apiClient;
        this.githubExecutorService = githubExecutorService;
    }

    private final GitHubApiClient apiClient;
    private final ExecutorService githubExecutorService;

    /**
     * Generates a full access report for the given GitHub organization.
     *
     * @param org GitHub organization name
     * @return AccessReport with user → repo mappings
     */
    public AccessReport generateReport(String org) {
        if (org == null || org.isBlank()) {
            throw new IllegalArgumentException("Organization name must not be blank.");
        }

        System.out.println("Starting access report generation for org: " + org);
        long startTime = System.currentTimeMillis();

        // Step 1: Fetch all repos (paginated)
        List<GitHubRepo> repos = apiClient.fetchAllRepos(org);
        System.out.println("Found " + repos.size() + " repositories in org: " + org);

        // Step 2: Fan out — fetch collaborators for all repos in PARALLEL
        List<CompletableFuture<RepoCollaboratorResult>> futures = repos.stream()
                .map(repo -> CompletableFuture.supplyAsync(
                        () -> fetchCollaboratorsForRepo(org, repo),
                        githubExecutorService
                ))
                .collect(Collectors.toList());

        // Step 3: Wait for all futures to complete
        List<RepoCollaboratorResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // Step 4: Aggregate into user → [repos] map
        Map<String, List<UserRepoAccess>> userAccessMap = aggregateByUser(results);

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Report generated in " + elapsed + "ms. Repos: " + repos.size() + " Users: " + userAccessMap.size());

        return AccessReport.builder()
                .organization(org)
                .generatedAt(Instant.now())
                .totalRepositories(repos.size())
                .totalUsers(userAccessMap.size())
                .userAccessMap(userAccessMap)
                .build();
    }

    /**
     * Fetches collaborators for a single repo and wraps result.
     * Designed to run inside the thread pool.
     */
    private RepoCollaboratorResult fetchCollaboratorsForRepo(String org, GitHubRepo repo) {
        List<GitHubCollaborator> collaborators = apiClient.fetchRepoCollaborators(org, repo.getName());
        return new RepoCollaboratorResult(repo, collaborators);
    }

    /**
     * Aggregates the flat list of (repo, collaborator) pairs
     * into a Map<username, List<UserRepoAccess>>.
     */
    private Map<String, List<UserRepoAccess>> aggregateByUser(List<RepoCollaboratorResult> results) {
        Map<String, List<UserRepoAccess>> userMap = new TreeMap<>(); // TreeMap for sorted output

        for (RepoCollaboratorResult result : results) {
            GitHubRepo repo = result.repo();
            for (GitHubCollaborator collaborator : result.collaborators()) {
                UserRepoAccess access = buildUserRepoAccess(repo, collaborator);
                userMap
                        .computeIfAbsent(collaborator.getLogin(), k -> new ArrayList<>())
                        .add(access);
            }
        }

        return userMap;
    }

    /**
     * Maps GitHub API models to our clean response model.
     */
    private UserRepoAccess buildUserRepoAccess(GitHubRepo repo, GitHubCollaborator collaborator) {
        RepoPermissions permissions = null;

        if (collaborator.getPermissions() != null) {
            GitHubCollaborator.Permissions p = collaborator.getPermissions();
            permissions = RepoPermissions.builder()
                    .pull(p.isPull())
                    .triage(p.isTriage())
                    .push(p.isPush())
                    .maintain(p.isMaintain())
                    .admin(p.isAdmin())
                    .build();
        }

        return UserRepoAccess.builder()
                .repoName(repo.getName())
                .repoFullName(repo.getFullName())
                .repoUrl(repo.getHtmlUrl())
                .isPrivate(repo.isPrivate())
                .role(collaborator.getRoleName())
                .permissions(permissions)
                .build();
    }

    /**
     * Internal record to carry repo + its collaborators together.
     */
    private record RepoCollaboratorResult(GitHubRepo repo, List<GitHubCollaborator> collaborators) {}
}
