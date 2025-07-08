package com.example.demogitlab.controller;

import com.example.demogitlab.dto.IssuesResponse;
import com.example.demogitlab.dto.ProjectDetailsResponse;
import com.example.demogitlab.dto.ProjectReleaseDataResponse;
import com.example.demogitlab.dto.PublicProjectsResponse;
import com.example.demogitlab.dto.ReleasesResponse;
import com.example.demogitlab.service.GitLabService;
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
import reactor.core.publisher.Mono;

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
     * Get release data for a GitLab project
     * 
     * @param projectPath the project path with namespace (e.g., "username/project-name")
     * @return the project release data
     */
    @Operation(
        summary = "Get release data for a GitLab project",
        description = "Returns detailed release data for the specified GitLab project, including issues, labels, and releases"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the project release data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectReleaseDataResponse.class))
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
    @GetMapping("/projects/{projectPath:.+}/releases")
    public Mono<ResponseEntity<ProjectReleaseDataResponse>> getProjectReleaseData(
            @Parameter(description = "Project path with namespace (e.g., 'username/project-name')", required = true)
            @PathVariable String projectPath) {
        log.info("Getting release data for project: {}", projectPath);
        return gitLabService.getProjectReleaseData(projectPath)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(error -> log.error("Error getting release data for project: {}", projectPath, error))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
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
    public Mono<ResponseEntity<PublicProjectsResponse>> getPublicProjects(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of projects per page", example = "20")
            @RequestParam(defaultValue = "20") int perPage) {
        log.info("Getting public projects (page: {}, perPage: {})", page, perPage);
        return gitLabService.getPublicProjects(page, perPage)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting public projects", error))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
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
    public Mono<ResponseEntity<ProjectDetailsResponse>> getProjectDetailsById(
            @Parameter(description = "Project ID", example = "71424563", required = true)
            @PathVariable Long projectId) {
        log.info("Getting project details for project ID: {}", projectId);
        return gitLabService.getProjectDetailsById(projectId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(error -> log.error("Error getting project details for project ID: {}", projectId, error))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
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
    public Mono<ResponseEntity<IssuesResponse>> getIssuesByProjectId(
            @Parameter(description = "Project ID", example = "71424563", required = true)
            @PathVariable Long projectId) {
        log.info("Getting issues for project ID: {}", projectId);
        return gitLabService.getIssuesByProjectId(projectId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(error -> log.error("Error getting issues for project ID: {}", projectId, error))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    /**
     * Get releases for a project by ID
     * 
     * @param projectId the project ID
     * @return the releases for the project
     */
    @Operation(
        summary = "Get releases for a project by ID",
        description = "Returns a list of releases for a GitLab project by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the releases",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReleasesResponse.class))
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
    @GetMapping("/projects/id/{projectId}/releases")
    public Mono<ResponseEntity<ReleasesResponse>> getReleasesByProjectId(
            @Parameter(description = "Project ID", example = "71424563", required = true)
            @PathVariable Long projectId) {
        log.info("Getting releases for project ID: {}", projectId);
        return gitLabService.getReleasesByProjectId(projectId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(error -> log.error("Error getting releases for project ID: {}", projectId, error))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
    }
}
