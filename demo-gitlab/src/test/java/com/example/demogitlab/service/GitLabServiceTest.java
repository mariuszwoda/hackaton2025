package com.example.demogitlab.service;

import com.example.demogitlab.client.GitLabClient;
import com.example.demogitlab.dto.IssuesResponse;
import com.example.demogitlab.dto.ProjectDetailsResponse;
import com.example.demogitlab.dto.ProjectReleaseDataResponse;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.model.GitLabIssue;
import com.example.demogitlab.model.GitLabProject;
import com.example.demogitlab.model.GitLabRelease;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitLabServiceTest {

    @Mock
    private GitLabClient gitLabClient;

    @InjectMocks
    private GitLabServiceImpl gitLabService;

    private GitLabProject mockProject;
    private GitLabRelease mockRelease;
    private GitLabIssue mockIssueOpen;
    private GitLabIssue mockIssueClosed;
    private List<String> mockLabels;

    @BeforeEach
    void setUp() {
        // Setup mock project
        mockProject = GitLabProject.builder()
                .id(123L)
                .name("Test Project")
                .pathWithNamespace("test/project")
                .webUrl("https://gitlab.com/test/project")
                .description("A test project")
                .build();

        // Setup mock release
        GitLabRelease.GitLabAuthor author = new GitLabRelease.GitLabAuthor();
        author.setName("Test Author");

        GitLabRelease.GitLabCommit commit = new GitLabRelease.GitLabCommit();
        commit.setId("abc123");
        commit.setTitle("Test Commit");

        mockRelease = GitLabRelease.builder()
                .name("Release 1.0")
                .tagName("v1.0")
                .description("First release")
                .createdAt(LocalDateTime.now().minusDays(7))
                .releasedAt(LocalDateTime.now().minusDays(5))
                .author(author)
                .commit(commit)
                .upcomingRelease(false)
                .build();

        // Setup mock issues
        GitLabIssue.GitLabMilestone milestone = new GitLabIssue.GitLabMilestone();
        milestone.setStartDate("2023-01-01");
        milestone.setDueDate("2023-12-31");

        mockIssueOpen = GitLabIssue.builder()
                .id(1L)
                .title("Open Issue")
                .state("open")
                .milestone(milestone)
                .build();

        mockIssueClosed = GitLabIssue.builder()
                .id(2L)
                .title("Closed Issue")
                .state("closed")
                .build();

        // Setup mock labels
        mockLabels = List.of("bug", "feature", "enhancement");
    }

    @Test
    void getPredefinedProjects_ShouldReturnListOfProjects() {
        // When
        List<String> projects = gitLabService.getPredefinedProjects();

        // Then
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.contains("mariuszwoda/hackaton2025project"));
    }

    @Test
    void getProjectReleaseData_ShouldReturnProjectData() {
        // Given
        String projectPath = "test/project";

        when(gitLabClient.getProject(anyString())).thenReturn(Mono.just(mockProject));
        when(gitLabClient.getReleases(anyLong())).thenReturn(Flux.just(mockRelease));
        when(gitLabClient.getIssues(anyLong())).thenReturn(Flux.just(mockIssueOpen, mockIssueClosed));
        when(gitLabClient.getLabels(anyLong())).thenReturn(Mono.just(mockLabels));

        // When
        Mono<ProjectReleaseDataResponse> result = gitLabService.getProjectReleaseData(projectPath);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(mockProject.getId(), response.getGitlabId());
                    assertEquals(mockProject.getName(), response.getProjectName());
                    assertEquals(mockProject.getPathWithNamespace(), response.getProjectPath());
                    assertEquals(mockProject.getWebUrl(), response.getWebUrl());
                    assertEquals(mockProject.getDescription(), response.getSummary());
                    assertEquals(2, response.getTotalIssueCount());

                    // Check issue status counts
                    Map<String, Integer> statusCounts = response.getIssueStatusCounts();
                    assertEquals(1, statusCounts.get("open"));
                    assertEquals(1, statusCounts.get("closed"));

                    // Check labels
                    assertEquals(mockLabels, response.getLabels());

                    // Check releases
                    assertEquals(1, response.getReleases().size());
                    ProjectReleaseDataResponse.ReleaseInfo releaseInfo = response.getReleases().get(0);
                    assertEquals(mockRelease.getName(), releaseInfo.getName());
                    assertEquals(mockRelease.getTagName(), releaseInfo.getTagName());
                    assertEquals(mockRelease.getDescription(), releaseInfo.getDescription());
                    assertEquals(mockRelease.getAuthor().getName(), releaseInfo.getAuthorName());
                    assertEquals(mockRelease.getCommit().getId(), releaseInfo.getCommitId());
                    assertEquals(mockRelease.getCommit().getTitle(), releaseInfo.getCommitTitle());
                    assertEquals(mockRelease.getUpcomingRelease(), releaseInfo.getUpcomingRelease());

                    // Check dates
                    assertNotNull(response.getStartDate());
                    assertNotNull(response.getDueDate());
                })
                .verifyComplete();
    }

    @Test
    void getProjectReleaseData_WhenProjectNotFound_ShouldReturnEmptyMono() {
        // Given
        String projectPath = "nonexistent/project";
        when(gitLabClient.getProject(anyString())).thenReturn(Mono.empty());

        // When
        Mono<ProjectReleaseDataResponse> result = gitLabService.getProjectReleaseData(projectPath);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getPublicProjects_ShouldReturnPublicProjects() {
        // Given
        int page = 1;
        int perPage = 20;

        GitLabProject mockProject1 = GitLabProject.builder()
                .id(1L)
                .name("Project 1")
                .pathWithNamespace("user1/project1")
                .webUrl("https://gitlab.com/user1/project1")
                .description("Description of Project 1")
                .build();

        GitLabProject mockProject2 = GitLabProject.builder()
                .id(2L)
                .name("Project 2")
                .pathWithNamespace("user2/project2")
                .webUrl("https://gitlab.com/user2/project2")
                .description("Description of Project 2")
                .build();

        when(gitLabClient.getPublicProjects(anyInt(), anyInt())).thenReturn(Flux.just(mockProject1, mockProject2));

        // When
        Mono<PublicProjectsResponse> result = gitLabService.getPublicProjects(page, perPage);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(page, response.getPage());
                    assertEquals(perPage, response.getPerPage());
                    assertEquals(2, response.getProjects().size());

                    PublicProjectsResponse.ProjectInfo projectInfo1 = response.getProjects().get(0);
                    assertEquals(mockProject1.getId(), projectInfo1.getId());
                    assertEquals(mockProject1.getName(), projectInfo1.getName());
                    assertEquals(mockProject1.getPathWithNamespace(), projectInfo1.getPathWithNamespace());
                    assertEquals(mockProject1.getWebUrl(), projectInfo1.getWebUrl());
                    assertEquals(mockProject1.getDescription(), projectInfo1.getDescription());

                    PublicProjectsResponse.ProjectInfo projectInfo2 = response.getProjects().get(1);
                    assertEquals(mockProject2.getId(), projectInfo2.getId());
                    assertEquals(mockProject2.getName(), projectInfo2.getName());
                    assertEquals(mockProject2.getPathWithNamespace(), projectInfo2.getPathWithNamespace());
                    assertEquals(mockProject2.getWebUrl(), projectInfo2.getWebUrl());
                    assertEquals(mockProject2.getDescription(), projectInfo2.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void getProjectDetailsById_ShouldReturnProjectDetails() {
        // Given
        Long projectId = 71424563L;

        // Use the existing mockProject with additional fields
        GitLabProject project = GitLabProject.builder()
                .id(projectId)
                .name("Hackaton 2025 Project")
                .description("A project for the 2025 Hackathon")
                .nameWithNamespace("Mariusz Woda / Hackaton 2025 Project")
                .path("hackaton2025project")
                .pathWithNamespace("mariuszwoda/hackaton2025project")
                .createdAt(LocalDateTime.now().minusDays(30))
                .defaultBranch("main")
                .sshUrlToRepo("git@gitlab.com:mariuszwoda/hackaton2025project.git")
                .httpUrlToRepo("https://gitlab.com/mariuszwoda/hackaton2025project.git")
                .webUrl("https://gitlab.com/mariuszwoda/hackaton2025project")
                .readmeUrl("https://gitlab.com/mariuszwoda/hackaton2025project/-/blob/main/README.md")
                .avatarUrl("https://gitlab.com/uploads/-/system/project/avatar/71424563/avatar.png")
                .starCount(5)
                .forksCount(2)
                .lastActivityAt(LocalDateTime.now().minusDays(1))
                .build();

        when(gitLabClient.getProjectById(anyLong())).thenReturn(Mono.just(project));

        // When
        Mono<ProjectDetailsResponse> result = gitLabService.getProjectDetailsById(projectId);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(project.getId(), response.getId());
                    assertEquals(project.getName(), response.getName());
                    assertEquals(project.getDescription(), response.getDescription());
                    assertEquals(project.getNameWithNamespace(), response.getNameWithNamespace());
                    assertEquals(project.getPath(), response.getPath());
                    assertEquals(project.getPathWithNamespace(), response.getPathWithNamespace());
                    assertEquals(project.getCreatedAt(), response.getCreatedAt());
                    assertEquals(project.getDefaultBranch(), response.getDefaultBranch());
                    assertEquals(project.getSshUrlToRepo(), response.getSshUrlToRepo());
                    assertEquals(project.getHttpUrlToRepo(), response.getHttpUrlToRepo());
                    assertEquals(project.getWebUrl(), response.getWebUrl());
                    assertEquals(project.getReadmeUrl(), response.getReadmeUrl());
                    assertEquals(project.getAvatarUrl(), response.getAvatarUrl());
                    assertEquals(project.getStarCount(), response.getStarCount());
                    assertEquals(project.getForksCount(), response.getForksCount());
                    assertEquals(project.getLastActivityAt(), response.getLastActivityAt());
                })
                .verifyComplete();
    }

    @Test
    void getProjectDetailsById_WhenProjectNotFound_ShouldReturnEmptyMono() {
        // Given
        Long projectId = 999999999L;
        when(gitLabClient.getProjectById(anyLong())).thenReturn(Mono.empty());

        // When
        Mono<ProjectDetailsResponse> result = gitLabService.getProjectDetailsById(projectId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getIssuesByProjectId_ShouldReturnIssues() {
        // Given
        Long projectId = 71424563L;

        // Create a list of GitLabIssue objects
        List<GitLabIssue> issues = List.of(
                GitLabIssue.builder()
                        .id(123456L)
                        .iid(1L)
                        .projectId(projectId)
                        .title("Fix login bug")
                        .description("Users are unable to log in when using Firefox")
                        .state("open")
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .updatedAt(LocalDateTime.now().minusDays(6))
                        .labels(List.of("bug", "critical", "frontend"))
                        .dueDate("2023-01-10")
                        .webUrl("https://gitlab.com/mariuszwoda/hackaton2025project/-/issues/1")
                        .author(new GitLabIssue.GitLabUser(1L, "John Doe", "johndoe", "active", "avatar-url", "web-url"))
                        .assignees(List.of(
                                new GitLabIssue.GitLabUser(2L, "Jane Smith", "janesmith", "active", "avatar-url", "web-url"),
                                new GitLabIssue.GitLabUser(3L, "Bob Johnson", "bobjohnson", "active", "avatar-url", "web-url")
                        ))
                        .build(),
                GitLabIssue.builder()
                        .id(123457L)
                        .iid(2L)
                        .projectId(projectId)
                        .title("Add new feature")
                        .description("Implement user profile page")
                        .state("closed")
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .updatedAt(LocalDateTime.now().minusDays(3))
                        .closedAt(LocalDateTime.now().minusDays(3))
                        .labels(List.of("feature", "frontend"))
                        .dueDate("2023-01-15")
                        .webUrl("https://gitlab.com/mariuszwoda/hackaton2025project/-/issues/2")
                        .author(new GitLabIssue.GitLabUser(2L, "Jane Smith", "janesmith", "active", "avatar-url", "web-url"))
                        .assignees(List.of(
                                new GitLabIssue.GitLabUser(1L, "John Doe", "johndoe", "active", "avatar-url", "web-url")
                        ))
                        .build()
        );

        when(gitLabClient.getIssues(anyLong())).thenReturn(Flux.fromIterable(issues));

        // When
        Mono<IssuesResponse> result = gitLabService.getIssuesByProjectId(projectId);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(projectId, response.getProjectId());
                    assertEquals(issues.size(), response.getTotalCount());
                    assertEquals(issues.size(), response.getIssues().size());

                    // Check first issue
                    IssuesResponse.IssueInfo issue1 = response.getIssues().get(0);
                    assertEquals(issues.get(0).getId(), issue1.getId());
                    assertEquals(issues.get(0).getIid(), issue1.getIid());
                    assertEquals(issues.get(0).getProjectId(), issue1.getProjectId());
                    assertEquals(issues.get(0).getTitle(), issue1.getTitle());
                    assertEquals(issues.get(0).getDescription(), issue1.getDescription());
                    assertEquals(issues.get(0).getState(), issue1.getState());
                    assertEquals(issues.get(0).getCreatedAt(), issue1.getCreatedAt());
                    assertEquals(issues.get(0).getUpdatedAt(), issue1.getUpdatedAt());
                    assertEquals(issues.get(0).getClosedAt(), issue1.getClosedAt());
                    assertEquals(issues.get(0).getLabels(), issue1.getLabels());
                    assertEquals(issues.get(0).getDueDate(), issue1.getDueDate());
                    assertEquals(issues.get(0).getWebUrl(), issue1.getWebUrl());
                    assertEquals(issues.get(0).getAuthor().getName(), issue1.getAuthorName());
                    assertEquals(2, issue1.getAssigneeNames().size());
                    assertTrue(issue1.getAssigneeNames().contains("Jane Smith"));
                    assertTrue(issue1.getAssigneeNames().contains("Bob Johnson"));

                    // Check second issue
                    IssuesResponse.IssueInfo issue2 = response.getIssues().get(1);
                    assertEquals(issues.get(1).getId(), issue2.getId());
                    assertEquals(issues.get(1).getIid(), issue2.getIid());
                    assertEquals(issues.get(1).getProjectId(), issue2.getProjectId());
                    assertEquals(issues.get(1).getTitle(), issue2.getTitle());
                    assertEquals(issues.get(1).getDescription(), issue2.getDescription());
                    assertEquals(issues.get(1).getState(), issue2.getState());
                    assertEquals(issues.get(1).getCreatedAt(), issue2.getCreatedAt());
                    assertEquals(issues.get(1).getUpdatedAt(), issue2.getUpdatedAt());
                    assertEquals(issues.get(1).getClosedAt(), issue2.getClosedAt());
                    assertEquals(issues.get(1).getLabels(), issue2.getLabels());
                    assertEquals(issues.get(1).getDueDate(), issue2.getDueDate());
                    assertEquals(issues.get(1).getWebUrl(), issue2.getWebUrl());
                    assertEquals(issues.get(1).getAuthor().getName(), issue2.getAuthorName());
                    assertEquals(1, issue2.getAssigneeNames().size());
                    assertTrue(issue2.getAssigneeNames().contains("John Doe"));
                })
                .verifyComplete();
    }

    @Test
    void getIssuesByProjectId_WhenProjectNotFound_ShouldReturnEmptyMono() {
        // Given
        Long projectId = 999999999L;
        when(gitLabClient.getIssues(anyLong())).thenReturn(Flux.empty());

        // When
        Mono<IssuesResponse> result = gitLabService.getIssuesByProjectId(projectId);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(projectId, response.getProjectId());
                    assertEquals(0, response.getTotalCount());
                    assertEquals(0, response.getIssues().size());
                })
                .verifyComplete();
    }
}
