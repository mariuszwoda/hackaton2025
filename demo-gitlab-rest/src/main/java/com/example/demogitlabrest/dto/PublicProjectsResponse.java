package com.example.demogitlabrest.dto;

import com.example.demogitlabrest.model.GitLabProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing public GitLab projects")
public class PublicProjectsResponse {

    @Schema(description = "List of GitLab projects")
    private List<ProjectInfo> projects;

    @Schema(description = "Current page number", example = "1")
    private int page;

    @Schema(description = "Number of projects per page", example = "20")
    private int perPage;

    @Schema(description = "Total number of projects", example = "100")
    private int totalCount;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Information about a GitLab project")
    public static class ProjectInfo {
        @Schema(description = "GitLab project ID", example = "12345")
        private Long id;

        @Schema(description = "Name of the GitLab project", example = "My Project")
        private String name;

        @Schema(description = "Path of the GitLab project with namespace", example = "username/project-name")
        private String pathWithNamespace;

        @Schema(description = "Web URL of the GitLab project", example = "https://gitlab.com/username/project-name")
        private String webUrl;

        @Schema(description = "Description of the project", example = "A project for demonstration purposes")
        private String description;

        /**
         * Create a ProjectInfo from a GitLabProject
         * 
         * @param project the GitLab project
         * @return a ProjectInfo object
         */
        public static ProjectInfo fromGitLabProject(GitLabProject project) {
            return ProjectInfo.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .pathWithNamespace(project.getPathWithNamespace())
                    .webUrl(project.getWebUrl())
                    .description(project.getDescription())
                    .build();
        }
    }
}