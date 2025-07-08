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
public class GitLabIssue {

    private Long id;
    private Long iid;
    
    @JsonProperty("project_id")
    private Long projectId;
    
    private String title;
    private String description;
    private String state;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    
    @JsonProperty("closed_by")
    private GitLabUser closedBy;
    
    private List<String> labels;
    
    private GitLabMilestone milestone;
    
    private List<GitLabUser> assignees;
    
    private GitLabUser author;
    
    @JsonProperty("assignee")
    private GitLabUser assignee;
    
    @JsonProperty("user_notes_count")
    private Integer userNotesCount;
    
    @JsonProperty("merge_requests_count")
    private Integer mergeRequestsCount;
    
    @JsonProperty("upvotes")
    private Integer upvotes;
    
    @JsonProperty("downvotes")
    private Integer downvotes;
    
    @JsonProperty("due_date")
    private String dueDate;
    
    @JsonProperty("confidential")
    private Boolean confidential;
    
    @JsonProperty("discussion_locked")
    private Boolean discussionLocked;
    
    @JsonProperty("web_url")
    private String webUrl;
    
    @JsonProperty("time_stats")
    private GitLabTimeStats timeStats;
    
    @JsonProperty("task_completion_status")
    private GitLabTaskCompletionStatus taskCompletionStatus;
    
    @JsonProperty("has_tasks")
    private Boolean hasTasks;
    
    @JsonProperty("_links")
    private GitLabIssueLinks links;
    
    @JsonProperty("references")
    private GitLabReferences references;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabUser {
        private Long id;
        private String name;
        private String username;
        
        @JsonProperty("state")
        private String state;
        
        @JsonProperty("avatar_url")
        private String avatarUrl;
        
        @JsonProperty("web_url")
        private String webUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabMilestone {
        private Long id;
        private Long iid;
        
        @JsonProperty("project_id")
        private Long projectId;
        
        private String title;
        private String description;
        private String state;
        
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        
        @JsonProperty("due_date")
        private String dueDate;
        
        @JsonProperty("start_date")
        private String startDate;
        
        @JsonProperty("web_url")
        private String webUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabTimeStats {
        
        @JsonProperty("time_estimate")
        private Integer timeEstimate;
        
        @JsonProperty("total_time_spent")
        private Integer totalTimeSpent;
        
        @JsonProperty("human_time_estimate")
        private String humanTimeEstimate;
        
        @JsonProperty("human_total_time_spent")
        private String humanTotalTimeSpent;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabTaskCompletionStatus {
        
        @JsonProperty("count")
        private Integer count;
        
        @JsonProperty("completed_count")
        private Integer completedCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabIssueLinks {
        
        @JsonProperty("self")
        private String self;
        
        @JsonProperty("notes")
        private String notes;
        
        @JsonProperty("award_emoji")
        private String awardEmoji;
        
        @JsonProperty("project")
        private String project;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitLabReferences {
        
        @JsonProperty("short")
        private String shortRef;
        
        @JsonProperty("relative")
        private String relative;
        
        @JsonProperty("full")
        private String full;
    }
}