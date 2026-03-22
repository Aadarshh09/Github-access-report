package com.cloudeagle.githubaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCollaborator {
    @JsonProperty("login")
    private String login;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("permissions")
    private Permissions permissions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Permissions {
        private boolean pull;
        private boolean triage;
        private boolean push;
        private boolean maintain;
        private boolean admin;
    }
}
