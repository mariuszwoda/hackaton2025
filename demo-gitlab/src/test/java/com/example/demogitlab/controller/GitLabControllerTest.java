package com.example.demogitlab.controller;

import com.example.demogitlab.dto.IssuesResponse;
import com.example.demogitlab.dto.ProjectDetailsResponse;
import com.example.demogitlab.dto.ProjectReleaseDataResponse;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.service.GitLabService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GitLabController.class)
public class GitLabControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitLabService gitLabService;

    @Test
    void getPredefinedProjects_ShouldReturnListOfProjects() {
        // Given
        List<String> projects = List.of("mariuszwoda/hackaton2025project");
        when(gitLabService.getPredefinedProjects()).thenReturn(projects);

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0]").isEqualTo("mariuszwoda/hackaton2025project");
    }

    @Test
    void getProjectReleaseData_ShouldReturnReleaseData() {
        // Given
        String projectPath = "mariuszwoda/hackaton2025project";
        Map<String, Integer> issueStatusCounts = new HashMap<>();
        issueStatusCounts.put("open", 5);
        issueStatusCounts.put("closed", 10);

        ProjectReleaseDataResponse.ReleaseInfo releaseInfo = ProjectReleaseDataResponse.ReleaseInfo.builder()
                .name("Release 1.0")
                .tagName("v1.0")
                .description("First release")
                .createdAt(LocalDateTime.now())
                .releasedAt(LocalDateTime.now())
                .authorName("John Doe")
                .commitId("abc123")
                .commitTitle("Initial commit")
                .upcomingRelease(false)
                .build();

        ProjectReleaseDataResponse response = ProjectReleaseDataResponse.builder()
                .gitlabId(123L)
                .projectName("Hackaton 2025 Project")
                .projectPath(projectPath)
                .webUrl("https://gitlab.com/mariuszwoda/hackaton2025project")
                .summary("A sample project for hackaton 2025")
                .totalIssueCount(15)
                .startDate(LocalDateTime.now().minusDays(30))
                .dueDate(LocalDateTime.now().plusDays(30))
                .issueStatusCounts(issueStatusCounts)
                .labels(List.of("bug", "feature", "enhancement"))
                .releases(List.of(releaseInfo))
                .build();

        when(gitLabService.getProjectReleaseData(anyString())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/{projectPath}/releases", projectPath)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectReleaseDataResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getProjectReleaseData_WhenProjectNotFound_ShouldReturnNotFound() {
        // Given
        String projectPath = "nonexistent/project";
        when(gitLabService.getProjectReleaseData(anyString())).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/{projectPath}/releases", projectPath)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getPublicProjects_ShouldReturnPublicProjects() {
        // Given
        int page = 1;
        int perPage = 20;

        List<PublicProjectsResponse.ProjectInfo> projects = List.of(
                PublicProjectsResponse.ProjectInfo.builder()
                        .id(1L)
                        .name("Project 1")
                        .pathWithNamespace("user1/project1")
                        .webUrl("https://gitlab.com/user1/project1")
                        .description("Description of Project 1")
                        .build(),
                PublicProjectsResponse.ProjectInfo.builder()
                        .id(2L)
                        .name("Project 2")
                        .pathWithNamespace("user2/project2")
                        .webUrl("https://gitlab.com/user2/project2")
                        .description("Description of Project 2")
                        .build()
        );

        PublicProjectsResponse response = PublicProjectsResponse.builder()
                .projects(projects)
                .page(page)
                .perPage(perPage)
                .totalCount(2)
                .totalPages(1)
                .build();

        when(gitLabService.getPublicProjects(anyInt(), anyInt())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/gitlab/projects/public")
                        .queryParam("page", page)
                        .queryParam("perPage", perPage)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PublicProjectsResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getProjectDetailsById_ShouldReturnProjectDetails() {
        // Given
        Long projectId = 71424563L;

        ProjectDetailsResponse response = ProjectDetailsResponse.builder()
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

        when(gitLabService.getProjectDetailsById(anyLong())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/id/{projectId}", projectId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDetailsResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getProjectDetailsById_WhenProjectNotFound_ShouldReturnNotFound() {
        // Given
        Long projectId = 999999999L;
        when(gitLabService.getProjectDetailsById(anyLong())).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/id/{projectId}", projectId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getIssuesByProjectId_ShouldReturnIssues() {
        // Given
        Long projectId = 71424563L;

        List<IssuesResponse.IssueInfo> issues = List.of(
                IssuesResponse.IssueInfo.builder()
                        .id(123456L)
                        .iid(1L)
                        .projectId(projectId)
                        .title("Fix login bug")
                        .description("Users are unable to log in when using Firefox")
                        .state("open")
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .updatedAt(LocalDateTime.now().minusDays(6))
                        .closedAt(null)
                        .labels(List.of("bug", "critical", "frontend"))
                        .dueDate("2023-01-10")
                        .webUrl("https://gitlab.com/mariuszwoda/hackaton2025project/-/issues/1")
                        .authorName("John Doe")
                        .assigneeNames(List.of("Jane Smith", "Bob Johnson"))
                        .build(),
                IssuesResponse.IssueInfo.builder()
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
                        .authorName("Jane Smith")
                        .assigneeNames(List.of("John Doe"))
                        .build()
        );

        IssuesResponse response = IssuesResponse.builder()
                .issues(issues)
                .totalCount(2)
                .projectId(projectId)
                .build();

        when(gitLabService.getIssuesByProjectId(anyLong())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/id/{projectId}/issues", projectId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IssuesResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getIssuesByProjectId_WhenProjectNotFound_ShouldReturnNotFound() {
        // Given
        Long projectId = 999999999L;
        when(gitLabService.getIssuesByProjectId(anyLong())).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects/id/{projectId}/issues", projectId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
