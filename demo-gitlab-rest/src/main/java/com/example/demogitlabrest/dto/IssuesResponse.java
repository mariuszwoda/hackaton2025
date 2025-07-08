package com.example.demogitlabrest.dto;

import com.example.demogitlabrest.model.GitLabIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing GitLab project issues")
public class IssuesResponse {

    @Schema(description = "List of issues in the project")
    private List<IssueInfo> issues;

    @Schema(description = "Total number of issues", example = "42")
    private int totalCount;

    @Schema(description = "Project ID", example = "12345")
    private Long projectId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Information about a GitLab issue")
    public static class IssueInfo {
        @Schema(description = "Issue ID", example = "12345")
        private Long id;

        @Schema(description = "Internal issue ID", example = "42")
        private Long iid;

        @Schema(description = "Project ID", example = "12345")
        private Long projectId;

        @Schema(description = "Title of the issue", example = "Fix login bug")
        private String title;

        @Schema(description = "Description of the issue", example = "Users are unable to log in when using Firefox")
        private String description;

        @Schema(description = "State of the issue (open, closed)", example = "open")
        private String state;

        @Schema(description = "Date and time when the issue was created", example = "2023-01-01T12:00:00")
        private LocalDateTime createdAt;

        @Schema(description = "Date and time when the issue was last updated", example = "2023-01-02T14:30:00")
        private LocalDateTime updatedAt;

        @Schema(description = "Date and time when the issue was closed", example = "2023-01-03T16:45:00")
        private LocalDateTime closedAt;

        @Schema(description = "Labels assigned to the issue", example = "[\"bug\", \"critical\", \"frontend\"]")
        private List<String> labels;

        @Schema(description = "Due date of the issue", example = "2023-01-10")
        private String dueDate;

        @Schema(description = "Web URL of the issue", example = "https://gitlab.com/username/my-project/-/issues/42")
        private String webUrl;

        @Schema(description = "Name of the author who created the issue", example = "John Doe")
        private String authorName;

        @Schema(description = "Names of users assigned to the issue", example = "[\"Jane Smith\", \"Bob Johnson\"]")
        private List<String> assigneeNames;

        /**
         * Create an IssueInfo from a GitLabIssue
         * 
         * @param issue the GitLab issue
         * @return an IssueInfo object
         */
        public static IssueInfo fromGitLabIssue(GitLabIssue issue) {
            List<String> assigneeNames = issue.getAssignees() != null
                    ? issue.getAssignees().stream()
                            .map(GitLabIssue.GitLabUser::getName)
                            .collect(Collectors.toList())
                    : null;

            return IssueInfo.builder()
                    .id(issue.getId())
                    .iid(issue.getIid())
                    .projectId(issue.getProjectId())
                    .title(issue.getTitle())
                    .description(issue.getDescription())
                    .state(issue.getState())
                    .createdAt(issue.getCreatedAt())
                    .updatedAt(issue.getUpdatedAt())
                    .closedAt(issue.getClosedAt())
                    .labels(issue.getLabels())
                    .dueDate(issue.getDueDate())
                    .webUrl(issue.getWebUrl())
                    .authorName(issue.getAuthor() != null ? issue.getAuthor().getName() : null)
                    .assigneeNames(assigneeNames)
                    .build();
        }
    }

    /**
     * Create an IssuesResponse from a list of GitLabIssues
     * 
     * @param issues the list of GitLab issues
     * @param projectId the project ID
     * @return an IssuesResponse object
     */
    public static IssuesResponse fromGitLabIssues(List<GitLabIssue> issues, Long projectId) {
        List<IssueInfo> issueInfos = issues.stream()
                .map(IssueInfo::fromGitLabIssue)
                .collect(Collectors.toList());

        return IssuesResponse.builder()
                .issues(issueInfos)
                .totalCount(issues.size())
                .projectId(projectId)
                .build();
    }
}