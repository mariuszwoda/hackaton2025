package com.example.demogitlabrest.controller;

import com.example.demogitlabrest.dto.IssuesResponse;
import com.example.demogitlabrest.dto.ProjectDetailsResponse;
import com.example.demogitlabrest.dto.ProjectReleaseDataResponse;
import com.example.demogitlabrest.dto.PublicProjectsResponse;
import com.example.demogitlabrest.dto.ReleasesResponse;
import com.example.demogitlabrest.service.GitLabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gitlab")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "GitLab API", description = "API for retrieving GitLab project and release data")
public class GitLabController {

    private final GitLabService gitLabService;

    /**
     * Get a list of predefined GitLab projects
     * 
     * @return a list of project paths
     */
    @Operation(
        summary = "Get predefined GitLab projects",
        description = "Returns a list of predefined GitLab project paths that can be used with the API"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of projects",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/projects")
    public ResponseEntity<List<String>> getPredefinedProjects() {
        log.info("Getting predefined GitLab projects");
        return ResponseEntity.ok(gitLabService.getPredefinedProjects());
    }

    /**
     * Get public GitLab projects
     * 
     * @param page the page number (1-based)
     * @param perPage the number of projects per page
     * @return a list of public GitLab projects
     */
    @Operation(
        summary = "Get public GitLab projects",
        description = "Returns a paginated list of public GitLab projects"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of public projects",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublicProjectsResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @GetMapping("/projects/public")
    public ResponseEntity<PublicProjectsResponse> getPublicProjects(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of projects per page", example = "20")
            @RequestParam(defaultValue = "20") int perPage) {
        log.info("Getting public projects (page: {}, perPage: {})", page, perPage);
        try {
            PublicProjectsResponse response = gitLabService.getPublicProjects(page, perPage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting public projects", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get project details by ID
     * 
     * @param projectId the project ID
     * @return the project details
     */
    @Operation(
        summary = "Get project details by ID",
        description = "Returns detailed information about a GitLab project by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the project details",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @GetMapping("/projects/id/{projectId}")
    public ResponseEntity<ProjectDetailsResponse> getProjectDetailsById(
            @Parameter(description = "Project ID", example = "71424563", required = true)
            @PathVariable Long projectId) {
        log.info("Getting project details for project ID: {}", projectId);
        try {
            ProjectDetailsResponse response = gitLabService.getProjectDetailsById(projectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting project details for project ID: {}", projectId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get issues for a project by ID
     * 
     * @param projectId the project ID
     * @return the issues for the project
     */
    @Operation(
        summary = "Get issues for a project by ID",
        description = "Returns a list of issues for a GitLab project by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the issues",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = IssuesResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @GetMapping("/projects/id/{projectId}/issues")
    public ResponseEntity<IssuesResponse> getIssuesByProjectId(
            @Parameter(description = "Project ID", example = "71424563", required = true)
            @PathVariable Long projectId) {
        log.info("Getting issues for project ID: {}", projectId);
        try {
            IssuesResponse response = gitLabService.getIssuesByProjectId(projectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting issues for project ID: {}", projectId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Note: The releases endpoint has been removed as per requirements
}