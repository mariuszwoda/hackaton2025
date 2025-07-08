# GitLab Release Data API

This Spring Boot application provides an API to fetch release data from GitLab projects. It is designed to be used by a frontend application (React SPA) to display release information.

## Features

- Fetch release data from GitLab projects
- Get a list of predefined GitLab projects
- Retrieve detailed information about releases, issues, and labels
- Cross-origin resource sharing (CORS) enabled for frontend integration
- Interactive API documentation with Swagger/OpenAPI

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection to access the GitLab API

## Building the Application

To build the application, run the following command:

```bash
mvn clean package
```

## Running the Application

To run the application, use the following command:

```bash
java -jar target/demo-gitlab-0.0.1-SNAPSHOT.jar
```

Alternatively, you can use the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default. You can access the API at http://localhost:8080/api/v1/gitlab.

## API Endpoints

### Get Predefined GitLab Projects

```
GET /api/v1/gitlab/projects
```

Returns a list of predefined GitLab project paths.

Example response:
```json
[
  "mariuszwoda/hackaton2025project"
]
```

### Get Public GitLab Projects

```
GET /api/v1/gitlab/projects/public
```

Returns a paginated list of public GitLab projects.

Example response:
```json
{
  "projects": [
    {
      "id": 1,
      "name": "Project 1",
      "pathWithNamespace": "user1/project1",
      "webUrl": "https://gitlab.com/user1/project1",
      "description": "Description of Project 1"
    },
    {
      "id": 2,
      "name": "Project 2",
      "pathWithNamespace": "user2/project2",
      "webUrl": "https://gitlab.com/user2/project2",
      "description": "Description of Project 2"
    }
  ],
  "page": 1,
  "perPage": 20,
  "totalCount": 2,
  "totalPages": 1
}
```

### Get Project Release Data

```
GET /api/v1/gitlab/projects/{projectPath}/releases
```

Returns detailed release data for the specified GitLab project.

Example response:
```json
{
  "gitlabId": 123,
  "projectName": "Hackaton 2025 Project",
  "projectPath": "mariuszwoda/hackaton2025project",
  "webUrl": "https://gitlab.com/mariuszwoda/hackaton2025project",
  "summary": "A sample project for hackaton 2025",
  "totalIssueCount": 15,
  "startDate": "2023-01-01T00:00:00",
  "dueDate": "2023-12-31T00:00:00",
  "issueStatusCounts": {
    "open": 5,
    "closed": 10
  },
  "labels": [
    "bug",
    "feature",
    "enhancement"
  ],
  "releases": [
    {
      "name": "Release 1.0",
      "tagName": "v1.0",
      "description": "First release",
      "createdAt": "2023-06-01T12:00:00",
      "releasedAt": "2023-06-01T12:00:00",
      "authorName": "John Doe",
      "commitId": "abc123",
      "commitTitle": "Initial commit",
      "upcomingRelease": false
    }
  ]
}
```

### Get Project Details by ID

```
GET /api/v1/gitlab/projects/id/{projectId}
```

Returns detailed information about a GitLab project by its ID.

Example response:
```json
{
  "id": 71424563,
  "name": "Hackaton 2025 Project",
  "description": "A project for the 2025 Hackathon",
  "nameWithNamespace": "Mariusz Woda / Hackaton 2025 Project",
  "path": "hackaton2025project",
  "pathWithNamespace": "mariuszwoda/hackaton2025project",
  "createdAt": "2023-01-01T12:00:00",
  "defaultBranch": "main",
  "sshUrlToRepo": "git@gitlab.com:mariuszwoda/hackaton2025project.git",
  "httpUrlToRepo": "https://gitlab.com/mariuszwoda/hackaton2025project.git",
  "webUrl": "https://gitlab.com/mariuszwoda/hackaton2025project",
  "readmeUrl": "https://gitlab.com/mariuszwoda/hackaton2025project/-/blob/main/README.md",
  "avatarUrl": "https://gitlab.com/uploads/-/system/project/avatar/71424563/avatar.png",
  "starCount": 5,
  "forksCount": 2,
  "lastActivityAt": "2023-06-01T14:30:00"
}
```

### Get Issues for a Project by ID

```
GET /api/v1/gitlab/projects/id/{projectId}/issues
```

Returns a list of issues for a GitLab project by its ID.

Example response:
```json
{
  "issues": [
    {
      "id": 123456,
      "iid": 1,
      "projectId": 71424563,
      "title": "Fix login bug",
      "description": "Users are unable to log in when using Firefox",
      "state": "open",
      "createdAt": "2023-01-01T12:00:00",
      "updatedAt": "2023-01-02T14:30:00",
      "closedAt": null,
      "labels": ["bug", "critical", "frontend"],
      "dueDate": "2023-01-10",
      "webUrl": "https://gitlab.com/mariuszwoda/hackaton2025project/-/issues/1",
      "authorName": "John Doe",
      "assigneeNames": ["Jane Smith", "Bob Johnson"]
    },
    {
      "id": 123457,
      "iid": 2,
      "projectId": 71424563,
      "title": "Add new feature",
      "description": "Implement user profile page",
      "state": "closed",
      "createdAt": "2023-01-03T10:00:00",
      "updatedAt": "2023-01-04T16:45:00",
      "closedAt": "2023-01-04T16:45:00",
      "labels": ["feature", "frontend"],
      "dueDate": "2023-01-15",
      "webUrl": "https://gitlab.com/mariuszwoda/hackaton2025project/-/issues/2",
      "authorName": "Jane Smith",
      "assigneeNames": ["John Doe"]
    }
  ],
  "totalCount": 2,
  "projectId": 71424563
}
```

### Get Releases for a Project by ID

```
GET /api/v1/gitlab/projects/id/{projectId}/releases
```

Returns a list of releases for a GitLab project by its ID.

Example response:
```json
{
  "releases": [
    {
      "name": "Release 1.0",
      "tagName": "v1.0",
      "description": "First release",
      "createdAt": "2023-06-01T12:00:00",
      "releasedAt": "2023-06-01T12:00:00",
      "authorName": "John Doe",
      "commitId": "abc123",
      "commitTitle": "Initial commit",
      "upcomingRelease": false
    },
    {
      "name": "Release 2.0",
      "tagName": "v2.0",
      "description": "Second release with new features",
      "createdAt": "2023-07-15T10:00:00",
      "releasedAt": "2023-07-15T14:30:00",
      "authorName": "Jane Smith",
      "commitId": "def456",
      "commitTitle": "Merge branch 'feature/new-feature' into 'main'",
      "upcomingRelease": false
    }
  ],
  "totalCount": 2,
  "projectId": 71424563
}
```

## API Documentation

The API is documented using OpenAPI (Swagger). You can access the documentation in the following ways:

### Swagger UI

The Swagger UI provides an interactive documentation of the API. You can access it at:

```
http://localhost:8080/swagger-ui.html
```

The Swagger UI allows you to:
- View all available endpoints
- See detailed information about request parameters and response models
- Test the API directly from the browser

### OpenAPI Specification

The OpenAPI specification in JSON format is available at:

```
http://localhost:8080/api-docs
```

You can use this specification with other tools like Postman or to generate client code.

## Configuration

The application can be configured using the following properties in `application.properties`:

```properties
# Server Configuration
server.port=8080
server.tomcat.relaxed-path-chars=[,],|,\,^,`,{,}
server.tomcat.relaxed-query-chars=[,],|,\,^,`,{,}
server.tomcat.allow-encoded-slash=true
server.tomcat.uri-encoding=UTF-8
spring.web.url-encoding=UTF-8

# GitLab API Configuration
gitlab.api.base-url=https://gitlab.com/api/v4
gitlab.api.timeout=30000

# Logging Configuration
logging.level.com.example.demogitlab=INFO

# Spring Configuration
spring.application.name=demo-gitlab
spring.jpa.open-in-view=false
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
spring.mvc.hiddenmethod.filter.enabled=true

# OpenAPI/Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.packages-to-scan=com.example.demogitlab.controller
```

### Handling URL-Encoded Slashes

This application includes special configuration to handle URL-encoded slashes in project paths. This is necessary because GitLab project paths contain slashes (e.g., "username/project-name"), which need to be properly encoded in URLs.

The following configurations are used to handle URL-encoded slashes:

1. In `application.properties`:
   - `server.tomcat.allow-encoded-slash=true` - Allows URL-encoded slashes in the path
   - `server.tomcat.relaxed-path-chars=[,],|,\,^,`,{,}` - Allows special characters in the path
   - `server.tomcat.relaxed-query-chars=[,],|,\,^,`,{,}` - Allows special characters in the query
   - `server.tomcat.uri-encoding=UTF-8` - Sets the URI encoding to UTF-8
   - `spring.web.url-encoding=UTF-8` - Sets the URL encoding to UTF-8
   - `spring.mvc.pathmatch.matching-strategy=ant_path_matcher` - Uses AntPathMatcher for path matching

2. In `WebConfig.java`:
   - A custom `TomcatServletWebServerFactory` bean is defined to configure the embedded Tomcat server to allow URL-encoded slashes in the path.

3. In `GitLabController.java`:
   - The `@GetMapping` annotation for the project release data endpoint uses a regex pattern `"/projects/{projectPath:.+}/releases"` to allow any character in the `projectPath` variable, including slashes.

4. In `GitLabClient.java`:
   - The `getProject` method replaces slashes with URL-encoded slashes (`%2F`) before passing the project path to the WebClient.

These configurations ensure that URLs with encoded slashes are properly handled by the application.

## Testing

To run the tests, use the following command:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
