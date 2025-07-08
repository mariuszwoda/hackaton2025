package com.example.demogitlab.service;

import com.example.demogitlab.dto.IssuesResponse;
import com.example.demogitlab.dto.ProjectDetailsResponse;
import com.example.demogitlab.dto.ProjectReleaseDataResponse;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.dto.ReleasesResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GitLabService {

    /**
     * Get a list of predefined GitLab projects
     * 
     * @return a list of project paths
     */
    List<String> getPredefinedProjects();

    /**
     * Get release data for a GitLab project
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @return a Mono containing the project release data
     */
    Mono<ProjectReleaseDataResponse> getProjectReleaseData(String projectPath);

    /**
     * Get public GitLab projects
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @return a Mono containing the public projects response
     */
    Mono<PublicProjectsResponse> getPublicProjects(int page, int perPage);

    /**
     * Get project details by ID
     * 
     * @param projectId the project ID
     * @return a Mono containing the project details
     */
    Mono<ProjectDetailsResponse> getProjectDetailsById(Long projectId);

    /**
     * Get issues for a project by ID
     * 
     * @param projectId the project ID
     * @return a Mono containing the issues response
     */
    Mono<IssuesResponse> getIssuesByProjectId(Long projectId);

    /**
     * Get releases for a project by ID
     * 
     * @param projectId the project ID
     * @return a Mono containing the releases response
     */
    Mono<ReleasesResponse> getReleasesByProjectId(Long projectId);
}
