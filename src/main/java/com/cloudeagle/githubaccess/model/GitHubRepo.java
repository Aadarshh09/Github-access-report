package com.cloudeagle.githubaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepo {
    @JsonProperty("id") private Long id;
    @JsonProperty("name") private String name;
    @JsonProperty("full_name") private String fullName;
    @JsonProperty("private") private boolean isPrivate;
    @JsonProperty("html_url") private String htmlUrl;
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public boolean isPrivate() { return isPrivate; }
    public String getHtmlUrl() { return htmlUrl; }
}