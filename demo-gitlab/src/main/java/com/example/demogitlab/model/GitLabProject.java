package com.example.demogitlab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitLabProject {

    private Long id;
    private String name;
    private String description;
    
    @JsonProperty("name_with_namespace")
    private String nameWithNamespace;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("path_with_namespace")
    private String pathWithNamespace;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("default_branch")
    private String defaultBranch;
    
    @JsonProperty("ssh_url_to_repo")
    private String sshUrlToRepo;
    
    @JsonProperty("http_url_to_repo")
    private String httpUrlToRepo;
    
    @JsonProperty("web_url")
    private String webUrl;
    
    @JsonProperty("readme_url")
    private String readmeUrl;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    @JsonProperty("star_count")
    private Integer starCount;
    
    @JsonProperty("forks_count")
    private Integer forksCount;
    
    @JsonProperty("last_activity_at")
    private LocalDateTime lastActivityAt;
}