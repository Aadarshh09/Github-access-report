package com.cloudeagle.githubaccess.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessReport {
    private String organization;
    private Instant generatedAt;
    private int totalRepositories;
    private int totalUsers;
    private Map<String, List<UserRepoAccess>> userAccessMap;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRepoAccess {
        private String repoName;
        private String repoFullName;
        private String repoUrl;
        private boolean isPrivate;
        private String role;
        private RepoPermissions permissions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepoPermissions {
        private boolean pull;
        private boolean triage;
        private boolean push;
        private boolean maintain;
        private boolean admin;
    }
}
