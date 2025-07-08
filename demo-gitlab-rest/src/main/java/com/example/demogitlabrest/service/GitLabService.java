package com.example.demogitlabrest.service;

import com.example.demogitlabrest.dto.IssuesResponse;
import com.example.demogitlabrest.dto.ProjectDetailsResponse;
import com.example.demogitlabrest.dto.ProjectReleaseDataResponse;
import com.example.demogitlabrest.dto.PublicProjectsResponse;
import com.example.demogitlabrest.dto.ReleasesResponse;

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
     * @return the project release data
     */
    ProjectReleaseDataResponse getProjectReleaseData(String projectPath);

    /**
     * Get public GitLab projects
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @return the public projects response
     */
    PublicProjectsResponse getPublicProjects(int page, int perPage);

    /**
     * Get project details by ID
     * 
     * @param projectId the project ID
     * @return the project details
     */
    ProjectDetailsResponse getProjectDetailsById(Long projectId);

    /**
     * Get issues for a project by ID
     * 
     * @param projectId the project ID
     * @return the issues response
     */
    IssuesResponse getIssuesByProjectId(Long projectId);

    /**
     * Get releases for a project by ID
     * 
     * @param projectId the project ID
     * @return the releases response
     */
    ReleasesResponse getReleasesByProjectId(Long projectId);
}