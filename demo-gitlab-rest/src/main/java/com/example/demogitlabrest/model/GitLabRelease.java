package com.example.demogitlabrest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitLabRelease {

    private String name;
    @JsonProperty("tag_name")
    private String tagName;
    private String description;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("released_at")
    private LocalDateTime releasedAt;

    @JsonProperty("author")
    private GitLabAuthor author;

    @JsonProperty("commit")
    private GitLabCommit commit;

    @JsonProperty("upcoming_release")
    private Boolean upcomingRelease;

    @JsonProperty("tag_path")
    private String tagPath;

    @JsonProperty("commit_path")
    private String commitPath;

    @JsonProperty("assets")
    private GitLabReleaseAssets assets;

    @JsonProperty("evidences")
    private List<GitLabEvidence> evidences;

    @JsonProperty("_links")
    private GitLabReleaseLinks links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabAuthor {
        private Long id;
        private String name;
        private String username;

        @JsonProperty("public_email")
        private String publicEmail;

        @JsonProperty("state")
        private String state;

        @JsonProperty("locked")
        private Boolean locked;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("web_url")
        private String webUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabCommit {
        private String id;

        @JsonProperty("short_id")
        private String shortId;

        private String title;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        private String message;

        @JsonProperty("author_name")
        private String authorName;

        @JsonProperty("author_email")
        private String authorEmail;

        @JsonProperty("authored_date")
        private LocalDateTime authoredDate;

        @JsonProperty("committer_name")
        private String committerName;

        @JsonProperty("committer_email")
        private String committerEmail;

        @JsonProperty("committed_date")
        private LocalDateTime committedDate;

        @JsonProperty("parent_ids")
        private List<String> parentIds;

        private Object trailers;

        @JsonProperty("extended_trailers")
        private Object extendedTrailers;

        @JsonProperty("web_url")
        private String webUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabReleaseAssets {
        private Integer count;
        private List<GitLabLink> links;
        private List<GitLabSource> sources;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabLink {
        private String name;
        private String url;

        @JsonProperty("link_type")
        private String linkType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabSource {
        private String format;
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabEvidence {
        private String sha;

        @JsonProperty("filepath")
        private String filepath;

        @JsonProperty("collected_at")
        private LocalDateTime collectedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabReleaseLinks {

        @JsonProperty("self")
        private String self;

        @JsonProperty("edit_url")
        private String editUrl;

        @JsonProperty("closed_issues_url")
        private String closedIssuesUrl;

        @JsonProperty("closed_merge_requests_url")
        private String closedMergeRequestsUrl;

        @JsonProperty("merged_merge_requests_url")
        private String mergedMergeRequestsUrl;

        @JsonProperty("opened_issues_url")
        private String openedIssuesUrl;

        @JsonProperty("opened_merge_requests_url")
        private String openedMergeRequestsUrl;
    }
}