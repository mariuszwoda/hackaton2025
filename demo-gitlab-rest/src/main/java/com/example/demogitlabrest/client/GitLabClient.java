package com.example.demogitlabrest.client;

import com.example.demogitlabrest.config.GitLabProperties;
import com.example.demogitlabrest.model.GitLabIssue;
import com.example.demogitlabrest.model.GitLabProject;
import com.example.demogitlabrest.model.GitLabRelease;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GitLabClient {

    private final RestTemplate gitLabRestTemplate;
    private final Map<String, RestTemplate> gitLabRestTemplates;
    private final GitLabProperties gitLabProperties;

    public GitLabClient(
            RestTemplate gitLabRestTemplate,
            @Qualifier("gitLabRestTemplates") Map<String, RestTemplate> gitLabRestTemplates,
            GitLabProperties gitLabProperties) {
        this.gitLabRestTemplate = gitLabRestTemplate;
        this.gitLabRestTemplates = gitLabRestTemplates;
        this.gitLabProperties = gitLabProperties;
    }

    /**
     * Get the RestTemplate for the specified GitLab instance
     * 
     * @param instanceName the name of the GitLab instance
     * @return the RestTemplate for the specified instance, or the default RestTemplate if not found
     */
    public RestTemplate getRestTemplate(String instanceName) {
        // Debug information
        log.debug("Getting RestTemplate for instance: {}", instanceName);
        log.debug("Available instances: {}", gitLabRestTemplates.keySet());
        log.debug("RestTemplate from map: {}", gitLabRestTemplates.get(instanceName));
        log.debug("Default RestTemplate: {}", gitLabRestTemplate);

        return gitLabRestTemplates.getOrDefault(instanceName, gitLabRestTemplate);
    }

    /**
     * Get the RestTemplate for the default GitLab instance
     * 
     * @return the RestTemplate for the default instance
     */
    public RestTemplate getDefaultRestTemplate() {
        return getRestTemplate(gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its path with namespace
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @return the GitLab project
     */
    public GitLabProject getProject(String projectPath) {
        return getProject(projectPath, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its path with namespace from a specific GitLab instance
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @param instanceName the name of the GitLab instance to use
     * @return the GitLab project
     */
    public GitLabProject getProject(String projectPath, String instanceName) {
        log.info("Fetching project by path: {} from instance: {}", projectPath, instanceName);
        
        String url = getBaseUrl(instanceName) + "/projects/{projectPath}";
        
        return getRestTemplate(instanceName).getForObject(
                url, 
                GitLabProject.class, 
                projectPath.replace("/", "%2F"));
    }

    /**
     * Get a project by its ID
     * 
     * @param projectId the project ID
     * @return the GitLab project
     */
    public GitLabProject getProjectById(Long projectId) {
        return getProjectById(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get a project by its ID from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return the GitLab project
     */
    public GitLabProject getProjectById(Long projectId, String instanceName) {
        log.info("Fetching project by ID: {} from instance: {}", projectId, instanceName);
        
        String url = getBaseUrl(instanceName) + "/projects/{projectId}";
        
        return getRestTemplate(instanceName).getForObject(
                url, 
                GitLabProject.class, 
                projectId);
    }

    /**
     * Get public projects
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @return a list of GitLab projects
     */
    public List<GitLabProject> getPublicProjects(int page, int perPage) {
        return getPublicProjects(page, perPage, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get public projects from a specific GitLab instance
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @param instanceName the name of the GitLab instance to use
     * @return a list of GitLab projects
     */
    public List<GitLabProject> getPublicProjects(int page, int perPage, String instanceName) {
        log.info("Fetching public projects (page: {}, perPage: {}) from instance: {}", page, perPage, instanceName);
        
        String url = UriComponentsBuilder.fromUriString(getBaseUrl(instanceName) + "/projects")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("order_by", "id")
                .queryParam("sort", "asc")
                .toUriString();
        
        ResponseEntity<GitLabProject[]> response = getRestTemplate(instanceName).getForEntity(
                url, 
                GitLabProject[].class);
        
        return response.getBody() != null ? Arrays.asList(response.getBody()) : Collections.emptyList();
    }

    /**
     * Get issues for a project
     * 
     * @param projectId the project ID
     * @return a list of GitLab issues
     */
    public List<GitLabIssue> getIssues(Long projectId) {
        return getIssues(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get issues for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a list of GitLab issues
     */
    public List<GitLabIssue> getIssues(Long projectId, String instanceName) {
        log.info("Fetching issues for project ID: {} from instance: {}", projectId, instanceName);
        
        String url = getBaseUrl(instanceName) + "/projects/{projectId}/issues";
        
        ResponseEntity<GitLabIssue[]> response = getRestTemplate(instanceName).getForEntity(
                url, 
                GitLabIssue[].class, 
                projectId);
        
        return response.getBody() != null ? Arrays.asList(response.getBody()) : Collections.emptyList();
    }

    /**
     * Get releases for a project
     * 
     * @param projectId the project ID
     * @return a list of GitLab releases
     */
    public List<GitLabRelease> getReleases(Long projectId) {
        return getReleases(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get releases for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a list of GitLab releases
     */
    public List<GitLabRelease> getReleases(Long projectId, String instanceName) {
        log.info("Fetching releases for project ID: {} from instance: {}", projectId, instanceName);
        
        String url = getBaseUrl(instanceName) + "/projects/{projectId}/releases";
        
        ResponseEntity<GitLabRelease[]> response = getRestTemplate(instanceName).getForEntity(
                url, 
                GitLabRelease[].class, 
                projectId);
        
        return response.getBody() != null ? Arrays.asList(response.getBody()) : Collections.emptyList();
    }

    /**
     * Get labels for a project
     * 
     * @param projectId the project ID
     * @return a list of label names
     */
    public List<String> getLabels(Long projectId) {
        return getLabels(projectId, gitLabProperties.getDefaultInstance());
    }

    /**
     * Get labels for a project from a specific GitLab instance
     * 
     * @param projectId the project ID
     * @param instanceName the name of the GitLab instance to use
     * @return a list of label names
     */
    public List<String> getLabels(Long projectId, String instanceName) {
        log.info("Fetching labels for project ID: {} from instance: {}", projectId, instanceName);
        
        String url = getBaseUrl(instanceName) + "/projects/{projectId}/labels";
        
        ResponseEntity<Map<String, String>[]> response = getRestTemplate(instanceName).exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>[]>() {},
                projectId);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(response.getBody())
                .map(label -> label.get("name"))
                .toList();
    }

    /**
     * Get the base URL for a GitLab instance
     * 
     * @param instanceName the name of the GitLab instance
     * @return the base URL for the GitLab API
     */
    private String getBaseUrl(String instanceName) {
        GitLabProperties.GitLabInstanceProperties props = gitLabProperties.getInstances().get(instanceName);
        return props != null ? props.getBaseUrl() : gitLabProperties.getBaseUrl();
    }
}