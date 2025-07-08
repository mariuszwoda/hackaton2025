package com.example.demogitlab.service;

import com.example.demogitlab.client.GitLabClient;
import com.example.demogitlab.dto.IssuesResponse;
import com.example.demogitlab.dto.ProjectDetailsResponse;
import com.example.demogitlab.dto.ProjectReleaseDataResponse;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.dto.ReleasesResponse;
import com.example.demogitlab.model.GitLabIssue;
import com.example.demogitlab.model.GitLabProject;
import com.example.demogitlab.model.GitLabRelease;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitLabServiceImpl implements GitLabService {

    private final GitLabClient gitLabClient;

    private static final List<String> PREDEFINED_PROJECTS = List.of(
            "mariuszwoda/hackaton2025project"
            // Add more predefined projects here
    );

    @Override
    public List<String> getPredefinedProjects() {
        return PREDEFINED_PROJECTS;
    }

    @Override
    public Mono<ProjectReleaseDataResponse> getProjectReleaseData(String projectPath) {
        log.info("Getting release data for project: {}", projectPath);

        return gitLabClient.getProject(projectPath)
                .flatMap(project -> {
                    Long projectId = project.getId();

                    Mono<List<GitLabRelease>> releasesMono = gitLabClient.getReleases(projectId)
                            .collectList();

                    Mono<List<GitLabIssue>> issuesMono = gitLabClient.getIssues(projectId)
                            .collectList();

                    Mono<List<String>> labelsMono = gitLabClient.getLabels(projectId);

                    return Mono.zip(
                            Mono.just(project),
                            releasesMono,
                            issuesMono,
                            labelsMono
                    ).map(tuple -> {
                        GitLabProject gitLabProject = tuple.getT1();
                        List<GitLabRelease> releases = tuple.getT2();
                        List<GitLabIssue> issues = tuple.getT3();
                        List<String> labels = tuple.getT4();

                        return buildProjectReleaseData(gitLabProject, releases, issues, labels);
                    });
                })
                .doOnSuccess(data -> log.info("Successfully retrieved release data for project: {}", projectPath))
                .doOnError(error -> log.error("Error retrieving release data for project: {}", projectPath, error));
    }

    /**
     * Helper method to parse date strings from GitLab API
     */
    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            // Try to parse as LocalDate first (YYYY-MM-DD)
            return LocalDate.parse(dateStr).atStartOfDay();
        } catch (DateTimeParseException e) {
            try {
                // Try to parse as LocalDateTime
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException ex) {
                log.warn("Failed to parse date: {}", dateStr);
                return null;
            }
        }
    }

    @Override
    public Mono<PublicProjectsResponse> getPublicProjects(int page, int perPage) {
        log.info("Getting public projects (page: {}, perPage: {})", page, perPage);

        return gitLabClient.getPublicProjects(page, perPage)
                .map(PublicProjectsResponse.ProjectInfo::fromGitLabProject)
                .collectList()
                .map(projectInfos -> {
                    // In a real implementation, we would get the total count from the GitLab API response headers
                    // For now, we'll estimate based on the current page results
                    int totalCount = projectInfos.size() == perPage ? page * perPage + perPage : page * perPage + projectInfos.size();
                    int totalPages = (int) Math.ceil((double) totalCount / perPage);

                    return PublicProjectsResponse.builder()
                            .projects(projectInfos)
                            .page(page)
                            .perPage(perPage)
                            .totalCount(totalCount)
                            .totalPages(totalPages)
                            .build();
                })
                .doOnSuccess(response -> log.info("Successfully retrieved {} public projects", response.getProjects().size()))
                .doOnError(error -> log.error("Error retrieving public projects", error));
    }

    @Override
    public Mono<ProjectDetailsResponse> getProjectDetailsById(Long projectId) {
        log.info("Getting project details for project ID: {}", projectId);

        return gitLabClient.getProjectById(projectId)
                .map(ProjectDetailsResponse::fromGitLabProject)
                .doOnSuccess(response -> log.info("Successfully retrieved project details for project ID: {}", projectId))
                .doOnError(error -> log.error("Error retrieving project details for project ID: {}", projectId, error));
    }

    @Override
    public Mono<IssuesResponse> getIssuesByProjectId(Long projectId) {
        log.info("Getting issues for project ID: {}", projectId);

        return gitLabClient.getIssues(projectId)
                .collectList()
                .map(issues -> IssuesResponse.fromGitLabIssues(issues, projectId))
                .doOnSuccess(response -> log.info("Successfully retrieved {} issues for project ID: {}", response.getTotalCount(), projectId))
                .doOnError(error -> log.error("Error retrieving issues for project ID: {}", projectId, error));
    }

    @Override
    public Mono<ReleasesResponse> getReleasesByProjectId(Long projectId) {
        log.info("Getting releases for project ID: {}", projectId);

        return gitLabClient.getReleases(projectId)
                .collectList()
                .map(releases -> ReleasesResponse.fromGitLabReleases(releases, projectId))
                .doOnSuccess(response -> log.info("Successfully retrieved {} releases for project ID: {}", response.getTotalCount(), projectId))
                .doOnError(error -> log.error("Error retrieving releases for project ID: {}", projectId, error));
    }

    private ProjectReleaseDataResponse buildProjectReleaseData(
            GitLabProject project,
            List<GitLabRelease> releases,
            List<GitLabIssue> issues,
            List<String> labels) {

        // Calculate issue status counts
        Map<String, Integer> issueStatusCounts = new HashMap<>();
        for (GitLabIssue issue : issues) {
            String status = issue.getState();
            issueStatusCounts.put(status, issueStatusCounts.getOrDefault(status, 0) + 1);
        }

        // Convert releases to ReleaseInfo objects
        List<ProjectReleaseDataResponse.ReleaseInfo> releaseInfos = releases.stream()
                .map(release -> ProjectReleaseDataResponse.ReleaseInfo.builder()
                        .name(release.getName())
                        .tagName(release.getTagName())
                        .description(release.getDescription())
                        .createdAt(release.getCreatedAt())
                        .releasedAt(release.getReleasedAt())
                        .authorName(release.getAuthor() != null ? release.getAuthor().getName() : null)
                        .commitId(release.getCommit() != null ? release.getCommit().getId() : null)
                        .commitTitle(release.getCommit() != null ? release.getCommit().getTitle() : null)
                        .upcomingRelease(release.getUpcomingRelease())
                        .build())
                .collect(Collectors.toList());

        // Find start and due dates from issues with milestones
        List<GitLabIssue.GitLabMilestone> milestones = issues.stream()
                .filter(issue -> issue.getMilestone() != null)
                .map(GitLabIssue::getMilestone)
                .collect(Collectors.toList());

        // Extract start and due dates from milestones if available
        LocalDateTime startDate = null;
        LocalDateTime dueDate = null;

        if (!milestones.isEmpty()) {
            // Find the earliest start date and latest due date
            for (GitLabIssue.GitLabMilestone milestone : milestones) {
                if (milestone.getStartDate() != null) {
                    LocalDateTime milestoneStartDate = parseDate(milestone.getStartDate());
                    if (milestoneStartDate != null && (startDate == null || milestoneStartDate.isBefore(startDate))) {
                        startDate = milestoneStartDate;
                    }
                }

                if (milestone.getDueDate() != null) {
                    LocalDateTime milestoneDueDate = parseDate(milestone.getDueDate());
                    if (milestoneDueDate != null && (dueDate == null || milestoneDueDate.isAfter(dueDate))) {
                        dueDate = milestoneDueDate;
                    }
                }
            }
        }

        // Build the response
        return ProjectReleaseDataResponse.builder()
                .gitlabId(project.getId())
                .projectName(project.getName())
                .projectPath(project.getPathWithNamespace())
                .webUrl(project.getWebUrl())
                .summary(project.getDescription())
                .totalIssueCount(issues.size())
                .startDate(startDate)
                .dueDate(dueDate)
                .issueStatusCounts(issueStatusCounts)
                .labels(labels)
                .releases(releaseInfos)
                .build();
    }
}
