package com.example.demogitlab.dto;

import com.example.demogitlab.model.GitLabProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing GitLab project details")
public class ProjectDetailsResponse {

    @Schema(description = "GitLab project ID", example = "12345")
    private Long id;

    @Schema(description = "Name of the GitLab project", example = "My Project")
    private String name;

    @Schema(description = "Description of the project", example = "A project for demonstration purposes")
    private String description;

    @Schema(description = "Name of the GitLab project with namespace", example = "Username / My Project")
    private String nameWithNamespace;

    @Schema(description = "Path of the GitLab project", example = "my-project")
    private String path;

    @Schema(description = "Path of the GitLab project with namespace", example = "username/my-project")
    private String pathWithNamespace;

    @Schema(description = "Date and time when the project was created", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Default branch of the project", example = "main")
    private String defaultBranch;

    @Schema(description = "SSH URL to the repository", example = "git@gitlab.com:username/my-project.git")
    private String sshUrlToRepo;

    @Schema(description = "HTTP URL to the repository", example = "https://gitlab.com/username/my-project.git")
    private String httpUrlToRepo;

    @Schema(description = "Web URL of the GitLab project", example = "https://gitlab.com/username/my-project")
    private String webUrl;

    @Schema(description = "URL to the project's README", example = "https://gitlab.com/username/my-project/-/blob/main/README.md")
    private String readmeUrl;

    @Schema(description = "URL to the project's avatar", example = "https://gitlab.com/uploads/-/system/project/avatar/12345/avatar.png")
    private String avatarUrl;

    @Schema(description = "Number of stars the project has", example = "42")
    private Integer starCount;

    @Schema(description = "Number of forks the project has", example = "10")
    private Integer forksCount;

    @Schema(description = "Date and time of the last activity on the project", example = "2023-06-01T14:30:00")
    private LocalDateTime lastActivityAt;

    /**
     * Create a ProjectDetailsResponse from a GitLabProject
     * 
     * @param project the GitLab project
     * @return a ProjectDetailsResponse object
     */
    public static ProjectDetailsResponse fromGitLabProject(GitLabProject project) {
        return ProjectDetailsResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .nameWithNamespace(project.getNameWithNamespace())
                .path(project.getPath())
                .pathWithNamespace(project.getPathWithNamespace())
                .createdAt(project.getCreatedAt())
                .defaultBranch(project.getDefaultBranch())
                .sshUrlToRepo(project.getSshUrlToRepo())
                .httpUrlToRepo(project.getHttpUrlToRepo())
                .webUrl(project.getWebUrl())
                .readmeUrl(project.getReadmeUrl())
                .avatarUrl(project.getAvatarUrl())
                .starCount(project.getStarCount())
                .forksCount(project.getForksCount())
                .lastActivityAt(project.getLastActivityAt())
                .build();
    }
}