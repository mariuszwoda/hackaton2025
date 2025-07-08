package com.example.demogitlab.client;

import com.example.demogitlab.config.GitLabProperties;
import com.example.demogitlab.model.GitLabIssue;
import com.example.demogitlab.model.GitLabProject;
import com.example.demogitlab.model.GitLabRelease;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GitLabClient {

    private final WebClient gitLabWebClient;
    private final Map<String, WebClient> gitLabWebClients;
    private final GitLabProperties gitLabProperties;

    public GitLabClient(
            WebClient gitLabWebClient,
            @Qualifier("gitLabWebClients") Map<String, WebClient> gitLabWebClients,
            GitLabProperties gitLabProperties) {
        this.gitLabWebClient = gitLabWebClient;
        this.gitLabWebClients = gitLabWebClients;
        this.gitLabProperties = gitLabProperties;
    }

    /**
     * Get the WebClient for the specified GitLab instance
     * 
     * @param instanceName the name of the GitLab instance
     * @return the WebClient for the specified instance, or the default WebClient if not found
     */
    public WebClient getWebClient(String instanceName) {
        // Debug information
        System.out.println("[DEBUG_LOG] Getting WebClient for instance: " + instanceName);
        System.out.println("[DEBUG_LOG] Available instances: " + gitLabWebClients.keySet());
        System.out.println("[DEBUG_LOG] WebClient from map: " + gitLabWebClients.get(instanceName));
        System.out.println("[DEBUG_LOG] Default WebClient: " + gitLabWebClient);

        return gitLabWebClients.getOrDefault(instanceName, gitLabWebClient);
    }

    /**
     * Get the WebClient for the default GitLab instance
     * 
     * @return the WebClient for the default instance
     */
    public WebClient getDefaultWebClient() {
        return getWebClient(gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its path with namespace
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @return a Mono containing the GitLab project
     */
    public Mono<GitLabProject> getProject(String projectPath) {
        return getProject(projectPath, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its path with namespace from a specific GitLab instance
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @param instanceName the name of the GitLab instance to use
     * @return a Mono containing the GitLab project
     */
    public Mono<GitLabProject> getProject(String projectPath, String instanceName) {
        log.info("Fetching project by path: {} from instance: {}", projectPath, instanceName);
        return getWebClient(instanceName).get()
                .uri("/projects/{projectPath}", projectPath.replace("/", "%2F"))
                .retrieve()
                .bodyToMono(GitLabProject.class)
                .doOnSuccess(project -> log.info("Successfully fetched project: {} from instance: {}", project.getName(), instanceName))
                .doOnError(error -> log.error("Error fetching project: {} from instance: {}", projectPath, instanceName, error));
    }

    /**
     * Get a project by its ID
     * 
     * @param projectId the project ID
     * @return a Mono containing the GitLab project
     */
    public Mono<GitLabProject> getProjectById(Long projectId) {
        return getProjectById(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its ID from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a Mono containing the GitLab project
     */
    public Mono<GitLabProject> getProjectById(Long projectId, String instanceName) {
        log.info("Fetching project by ID: {} from instance: {}", projectId, instanceName);
        return getWebClient(instanceName).get()
                .uri("/projects/{projectId}", projectId)
                .retrieve()
                .bodyToMono(GitLabProject.class)
                .doOnSuccess(project -> log.info("Successfully fetched project: {} from instance: {}", project.getName(), instanceName))
                .doOnError(error -> log.error("Error fetching project with ID: {} from instance: {}", projectId, instanceName, error));
    }

    /**
     * Get all releases for a project
     * 
     * @param projectId the project ID
     * @return a Flux containing the GitLab releases
     */
    public Flux<GitLabRelease> getReleases(Long projectId) {
        return getReleases(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get all releases for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a Flux containing the GitLab releases
     */
    public Flux<GitLabRelease> getReleases(Long projectId, String instanceName) {
        log.info("Fetching releases for project ID: {} from instance: {}", projectId, instanceName);
        return getWebClient(instanceName).get()
                .uri("/projects/{projectId}/releases", projectId)
                .retrieve()
                .bodyToFlux(GitLabRelease.class)
                .doOnComplete(() -> log.info("Successfully fetched releases for project ID: {} from instance: {}", projectId, instanceName))
                .doOnError(error -> log.error("Error fetching releases for project ID: {} from instance: {}", projectId, instanceName, error));
    }

    /**
     * Get all issues for a project
     * 
     * @param projectId the project ID
     * @return a Flux containing the GitLab issues
     */
    public Flux<GitLabIssue> getIssues(Long projectId) {
        return getIssues(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get all issues for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a Flux containing the GitLab issues
     */
    public Flux<GitLabIssue> getIssues(Long projectId, String instanceName) {
        log.info("Fetching issues for project ID: {} from instance: {}", projectId, instanceName);
        return getWebClient(instanceName).get()
                .uri("/projects/{projectId}/issues", projectId)
                .retrieve()
                .bodyToFlux(GitLabIssue.class)
                .doOnComplete(() -> log.info("Successfully fetched issues for project ID: {} from instance: {}", projectId, instanceName))
                .doOnError(error -> log.error("Error fetching issues for project ID: {} from instance: {}", projectId, instanceName, error));
    }

    /**
     * Get all labels for a project
     * 
     * @param projectId the project ID
     * @return a Mono containing a list of label names
     */
    public Mono<List<String>> getLabels(Long projectId) {
        return getLabels(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get all labels for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a Mono containing a list of label names
     */
    public Mono<List<String>> getLabels(Long projectId, String instanceName) {
        log.info("Fetching labels for project ID: {} from instance: {}", projectId, instanceName);
        return getWebClient(instanceName).get()
                .uri("/projects/{projectId}/labels", projectId)
                .retrieve()
                .bodyToFlux(GitLabLabel.class)
                .map(GitLabLabel::getName)
                .collectList()
                .doOnSuccess(labels -> log.info("Successfully fetched {} labels for project ID: {} from instance: {}", labels.size(), projectId, instanceName))
                .doOnError(error -> log.error("Error fetching labels for project ID: {} from instance: {}", projectId, instanceName, error));
    }

    /**
     * Get all public projects from GitLab
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @return a Flux containing the GitLab projects
     */
    public Flux<GitLabProject> getPublicProjects(int page, int perPage) {
        return getPublicProjects(page, perPage, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get all public projects from a specific GitLab instance
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @param instanceName the name of the GitLab instance to use
     * @return a Flux containing the GitLab projects
     */
    public Flux<GitLabProject> getPublicProjects(int page, int perPage, String instanceName) {
        log.info("Fetching public projects (page: {}, perPage: {}) from instance: {}", page, perPage, instanceName);

        Map<String, Object> queryParams = Map.of(
            "visibility", "public",
            "order_by", "name",
            "sort", "asc",
            "page", page,
            "per_page", perPage
        );

        return getWebClient(instanceName).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/projects")
                        .queryParam("visibility", "{visibility}")
                        .queryParam("order_by", "{order_by}")
                        .queryParam("sort", "{sort}")
                        .queryParam("page", "{page}")
                        .queryParam("per_page", "{per_page}")
                        .build(queryParams))
                .retrieve()
                .bodyToFlux(GitLabProject.class)
                .doOnComplete(() -> log.info("Successfully fetched public projects (page: {}, perPage: {}) from instance: {}", page, perPage, instanceName))
                .doOnError(error -> log.error("Error fetching public projects from instance: {}", instanceName, error));
    }

    @lombok.Data
    private static class GitLabLabel {
        private String name;
        private String color;
        private String description;
    }
}
