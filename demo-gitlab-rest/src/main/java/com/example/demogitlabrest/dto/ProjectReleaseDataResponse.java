package com.example.demogitlabrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing GitLab project release data")
public class ProjectReleaseDataResponse {

    @Schema(description = "GitLab project ID", example = "12345")
    private Long gitlabId;

    @Schema(description = "Name of the GitLab project", example = "My Project")
    private String projectName;

    @Schema(description = "Path of the GitLab project with namespace", example = "username/project-name")
    private String projectPath;

    @Schema(description = "Web URL of the GitLab project", example = "https://gitlab.com/username/project-name")
    private String webUrl;

    @Schema(description = "Summary or description of the project", example = "A project for demonstration purposes")
    private String summary;

    @Schema(description = "Total number of issues in the project", example = "42")
    private Integer totalIssueCount;

    @Schema(description = "Start date of the project (from milestones)", example = "2023-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Due date of the project (from milestones)", example = "2023-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Counts of issues by status", example = "{\"open\": 5, \"closed\": 10}")
    private Map<String, Integer> issueStatusCounts;

    @Schema(description = "List of labels used in the project", example = "[\"bug\", \"feature\", \"enhancement\"]")
    private List<String> labels;

    @Schema(description = "List of releases in the project")
    private List<ReleaseInfo> releases;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Information about a GitLab release")
    public static class ReleaseInfo {
        @Schema(description = "Name of the release", example = "Release 1.0")
        private String name;

        @Schema(description = "Tag name of the release", example = "v1.0.0")
        private String tagName;

        @Schema(description = "Description of the release", example = "Initial release with core features")
        private String description;

        @Schema(description = "Date and time when the release was created", example = "2023-06-01T12:00:00")
        private LocalDateTime createdAt;

        @Schema(description = "Date and time when the release was published", example = "2023-06-01T14:30:00")
        private LocalDateTime releasedAt;

        @Schema(description = "Name of the author who created the release", example = "John Doe")
        private String authorName;

        @Schema(description = "Commit ID associated with the release", example = "a1b2c3d4e5f6g7h8i9j0")
        private String commitId;

        @Schema(description = "Title of the commit associated with the release", example = "Merge branch 'feature/new-feature' into 'main'")
        private String commitTitle;

        @Schema(description = "Indicates if this is an upcoming release", example = "false")
        private Boolean upcomingRelease;
    }
}