package com.cloudeagle.githubaccess.model;
import java.time.Instant;
import java.util.List;
import java.util.Map;
public class AccessReport {
    private String organization;
    private Instant generatedAt;
    private int totalRepositories;
    private int totalUsers;
    private Map<String, List<UserRepoAccess>> userAccessMap;
    public static Builder builder() { return new Builder(); }
    public String getOrganization() { return organization; }
    public Instant getGeneratedAt() { return generatedAt; }
    public int getTotalRepositories() { return totalRepositories; }
    public int getTotalUsers() { return totalUsers; }
    public Map<String, List<UserRepoAccess>> getUserAccessMap() { return userAccessMap; }
    public static class Builder {
        private final AccessReport r = new AccessReport();
        public Builder organization(String v) { r.organization = v; return this; }
        public Builder generatedAt(Instant v) { r.generatedAt = v; return this; }
        public Builder totalRepositories(int v) { r.totalRepositories = v; return this; }
        public Builder totalUsers(int v) { r.totalUsers = v; return this; }
        public Builder userAccessMap(Map<String, List<UserRepoAccess>> v) { r.userAccessMap = v; return this; }
        public AccessReport build() { return r; }
    }
    public static class UserRepoAccess {
        private String repoName;
        private String repoFullName;
        private String repoUrl;
        private boolean isPrivate;
        private String role;
        private RepoPermissions permissions;
        public static Builder builder() { return new Builder(); }
        public String getRepoName() { return repoName; }
        public String getRepoFullName() { return repoFullName; }
        public String getRepoUrl() { return repoUrl; }
        public boolean isPrivate() { return isPrivate; }
        public String getRole() { return role; }
        public RepoPermissions getPermissions() { return permissions; }
        public static class Builder {
            private final UserRepoAccess o = new UserRepoAccess();
            public Builder repoName(String v) { o.repoName = v; return this; }
            public Builder repoFullName(String v) { o.repoFullName = v; return this; }
            public Builder repoUrl(String v) { o.repoUrl = v; return this; }
            public Builder isPrivate(boolean v) { o.isPrivate = v; return this; }
            public Builder role(String v) { o.role = v; return this; }
            public Builder permissions(RepoPermissions v) { o.permissions = v; return this; }
            public UserRepoAccess build() { return o; }
        }
    }
    public static class RepoPermissions {
        private boolean pull, triage, push, maintain, admin;
        public static Builder builder() { return new Builder(); }
        public boolean isPull() { return pull; }
        public boolean isTriage() { return triage; }
        public boolean isPush() { return push; }
        public boolean isMaintain() { return maintain; }
        public boolean isAdmin() { return admin; }
        public static class Builder {
            private final RepoPermissions o = new RepoPermissions();
            public Builder pull(boolean v) { o.pull = v; return this; }
            public Builder triage(boolean v) { o.triage = v; return this; }
            public Builder push(boolean v) { o.push = v; return this; }
            public Builder maintain(boolean v) { o.maintain = v; return this; }
            public Builder admin(boolean v) { o.admin = v; return this; }
            public RepoPermissions build() { return o; }
        }
    }
}
