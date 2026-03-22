package com.cloudeagle.githubaccess.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCollaborator {
    @JsonProperty("login") private String login;
    @JsonProperty("id") private Long id;
    @JsonProperty("html_url") private String htmlUrl;
    @JsonProperty("role_name") private String roleName;
    @JsonProperty("permissions") private Permissions permissions;
    public String getLogin() { return login; }
    public Long getId() { return id; }
    public String getHtmlUrl() { return htmlUrl; }
    public String getRoleName() { return roleName; }
    public Permissions getPermissions() { return permissions; }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Permissions {
        private boolean pull;
        private boolean triage;
        private boolean push;
        private boolean maintain;
        private boolean admin;
        public boolean isPull() { return pull; }
        public boolean isTriage() { return triage; }
        public boolean isPush() { return push; }
        public boolean isMaintain() { return maintain; }
        public boolean isAdmin() { return admin; }
    }
}
