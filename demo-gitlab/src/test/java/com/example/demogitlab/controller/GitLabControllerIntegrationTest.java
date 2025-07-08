package com.example.demogitlab.controller;

import com.example.demogitlab.client.GitLabClient;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.model.GitLabIssue;
import com.example.demogitlab.model.GitLabProject;
import com.example.demogitlab.model.GitLabRelease;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class GitLabControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitLabClient gitLabClient;

    @Test
    void getPredefinedProjects_ShouldReturnProjects() {
        // When & Then
        webTestClient.get()
                .uri("/api/v1/gitlab/projects")
                .accept(MediaType.APPLICATION_JSON)
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
        String projectPath = "test/project";

        // Setup mock project
        GitLabProject mockProject = GitLabProject.builder()
                .id(123L)
                .name("Test Project")
                .pathWithNamespace(projectPath)
                .webUrl("https://gitlab.com/test/project")
                .description("A test project")
                .build();

        // Setup mock release
        GitLabRelease.GitLabAuthor author = new GitLabRelease.GitLabAuthor();
        author.setName("Test Author");

        GitLabRelease.GitLabCommit commit = new GitLabRelease.GitLabCommit();
        commit.setId("abc123");
        commit.setTitle("Test Commit");

        GitLabRelease mockRelease = GitLabRelease.builder()
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

        GitLabIssue mockIssueOpen = GitLabIssue.builder()
                .id(1L)
                .title("Open Issue")
                .state("open")
                .milestone(milestone)
                .build();

        GitLabIssue mockIssueClosed = GitLabIssue.builder()
                .id(2L)
                .title("Closed Issue")
                .state("closed")
                .build();

        // Setup mock labels
        List<String> mockLabels = List.of("bug", "feature", "enhancement");

        // Mock client responses
        when(gitLabClient.getProject(anyString())).thenReturn(Mono.just(mockProject));
        when(gitLabClient.getReleases(anyLong())).thenReturn(Flux.just(mockRelease));
        when(gitLabClient.getIssues(anyLong())).thenReturn(Flux.just(mockIssueOpen, mockIssueClosed));
        when(gitLabClient.getLabels(anyLong())).thenReturn(Mono.just(mockLabels));

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/gitlab/projects/{projectPath}/releases")
                        .build(projectPath.replace("/", "%2F")))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gitlabId").isEqualTo(123)
                .jsonPath("$.projectName").isEqualTo("Test Project")
                .jsonPath("$.projectPath").isEqualTo(projectPath)
                .jsonPath("$.webUrl").isEqualTo("https://gitlab.com/test/project")
                .jsonPath("$.summary").isEqualTo("A test project")
                .jsonPath("$.totalIssueCount").isEqualTo(2)
                .jsonPath("$.issueStatusCounts.open").isEqualTo(1)
                .jsonPath("$.issueStatusCounts.closed").isEqualTo(1)
                .jsonPath("$.labels").isArray()
                .jsonPath("$.labels.length()").isEqualTo(3)
                .jsonPath("$.releases").isArray()
                .jsonPath("$.releases.length()").isEqualTo(1)
                .jsonPath("$.releases[0].name").isEqualTo("Release 1.0")
                .jsonPath("$.releases[0].tagName").isEqualTo("v1.0")
                .jsonPath("$.releases[0].description").isEqualTo("First release")
                .jsonPath("$.releases[0].authorName").isEqualTo("Test Author")
                .jsonPath("$.releases[0].commitId").isEqualTo("abc123")
                .jsonPath("$.releases[0].commitTitle").isEqualTo("Test Commit")
                .jsonPath("$.releases[0].upcomingRelease").isEqualTo(false);
    }

    @Test
    void getProjectReleaseData_WhenProjectNotFound_ShouldReturnNotFound() {
        // Given
        String projectPath = "nonexistent/project";
        when(gitLabClient.getProject(anyString())).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/gitlab/projects/{projectPath}/releases")
                        .build(projectPath.replace("/", "%2F")))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void swaggerUiEndpoint_ShouldBeAccessible() {
        // When & Then
        webTestClient.get()
                .uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void openApiDocsEndpoint_ShouldBeAccessible() {
        // When & Then
        webTestClient.get()
                .uri("/api-docs")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getPublicProjects_ShouldReturnPublicProjects() {
        // Given
        int page = 1;
        int perPage = 20;

        // Setup mock projects
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

        // Mock client response
        when(gitLabClient.getPublicProjects(anyInt(), anyInt())).thenReturn(Flux.just(mockProject1, mockProject2));

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/gitlab/projects/public")
                        .queryParam("page", page)
                        .queryParam("perPage", perPage)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projects").isArray()
                .jsonPath("$.projects.length()").isEqualTo(2)
                .jsonPath("$.projects[0].id").isEqualTo(1)
                .jsonPath("$.projects[0].name").isEqualTo("Project 1")
                .jsonPath("$.projects[0].pathWithNamespace").isEqualTo("user1/project1")
                .jsonPath("$.projects[1].id").isEqualTo(2)
                .jsonPath("$.projects[1].name").isEqualTo("Project 2")
                .jsonPath("$.projects[1].pathWithNamespace").isEqualTo("user2/project2")
                .jsonPath("$.page").isEqualTo(page)
                .jsonPath("$.perPage").isEqualTo(perPage);
    }
}
