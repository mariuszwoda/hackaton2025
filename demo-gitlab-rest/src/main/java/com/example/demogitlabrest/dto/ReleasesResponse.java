package com.example.demogitlabrest.dto;

import com.example.demogitlabrest.model.GitLabRelease;
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
@Schema(description = "Response containing GitLab project releases")
public class ReleasesResponse {

    @Schema(description = "List of releases in the project")
    private List<ReleaseInfo> releases;

    @Schema(description = "Total number of releases", example = "5")
    private int totalCount;

    @Schema(description = "Project ID", example = "12345")
    private Long projectId;

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

        /**
         * Create a ReleaseInfo from a GitLabRelease
         * 
         * @param release the GitLab release
         * @return a ReleaseInfo object
         */
        public static ReleaseInfo fromGitLabRelease(GitLabRelease release) {
            return ReleaseInfo.builder()
                    .name(release.getName())
                    .tagName(release.getTagName())
                    .description(release.getDescription())
                    .createdAt(release.getCreatedAt())
                    .releasedAt(release.getReleasedAt())
                    .authorName(release.getAuthor() != null ? release.getAuthor().getName() : null)
                    .commitId(release.getCommit() != null ? release.getCommit().getId() : null)
                    .commitTitle(release.getCommit() != null ? release.getCommit().getTitle() : null)
                    .upcomingRelease(release.getUpcomingRelease())
                    .build();
        }
    }

    /**
     * Create a ReleasesResponse from a list of GitLabReleases
     * 
     * @param releases the list of GitLab releases
     * @param projectId the project ID
     * @return a ReleasesResponse object
     */
    public static ReleasesResponse fromGitLabReleases(List<GitLabRelease> releases, Long projectId) {
        List<ReleaseInfo> releaseInfos = releases.stream()
                .map(ReleaseInfo::fromGitLabRelease)
                .collect(Collectors.toList());

        return ReleasesResponse.builder()
                .releases(releaseInfos)
                .totalCount(releases.size())
                .projectId(projectId)
                .build();
    }
}