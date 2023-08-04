# GameBuddy Community Service
GameBuddy Community Service is a microservice in the GameBuddy project. The service is built using Spring Java and provides a set of RESTful APIs to handle various functionalities related to user communities, posts, comments, likes, and more.


## APIs (Soon I will prepare the swagger document.)

### Community APIs

##### GET /community/get/communities

- Description: Retrieve the list of communities that the user is a member of.
- Request Header: Authorization (Bearer Token)
- Response: CommunityResponse

##### GET /community/get/members/{communityId}

- Description: Get the list of members in a specific community.
- Request Header: Authorization (Bearer Token)
- Path Variable: communityId (The ID of the community)
- Response: MemberResponse

##### GET /community/get/posts/{communityId}

- Description: Get the list of posts in a specific community.
- Request Header: Authorization (Bearer Token)
- Path Variable: communityId (The ID of the community)
- Response: PostResponse

##### GET /community/get/post/likes/{postId}

- Description: Get the list of users who liked a specific post.
- Request Header: Authorization (Bearer Token)
- Path Variable: postId (The ID of the post)
- Response: MemberResponse

##### GET /community/get/comment/likes/{commentId}

- Description: Get the list of users who liked a specific comment.
- Request Header: Authorization (Bearer Token)
- Path Variable: commentId (The ID of the comment)
- Response: MemberResponse

##### GET /community/get/post/comments/{postId}

- Description: Get the list of comments on a post.
- Request Header: Authorization (Bearer Token)
- Path Variable: postId (The ID of the post)
- Response: CommentsResponse



### Joined Community APIs

##### GET /community/get/joined/posts

- Description: Get the list of posts from all the communities that the user has joined.
- Request Header: Authorization (Bearer Token)
- Response: PostResponse



### Create APIs

##### POST /community/create

- Description: Create a new community.
- Request Header: Authorization (Bearer Token)
- Request Body: CreateCommunityRequest
- Response: DefaultMessageResponse

##### POST /community/create/post

- Description: Create a new post in a community.
- Request Header: Authorization (Bearer Token)
- Request Body: PostRequest
- Response: DefaultMessageResponse

##### POST /community/create/comment

- Description: Create a new comment on a post.
- Request Header: Authorization (Bearer Token)
- Request Body: CreateCommentRequest
- Response: DefaultMessageResponse



### Delete APIs

##### DELETE /community/delete

- Description: Delete a community (requires admin privileges or the community owner).
- Request Header: Authorization (Bearer Token)
- Request Body: CommunityRequest
- Response: DefaultMessageResponse

##### DELETE /community/delete/post/{postId}

- Description: Delete a post in a community (requires post owner or admin privileges).
- Request Header: Authorization (Bearer Token)
- Path Variable: postId (The ID of the post to be deleted)
- Response: DefaultMessageResponse

##### DELETE /community/delete/comment/{commentId}

- Description: Delete a comment on a post (requires comment owner or admin privileges).
- Request Header: Authorization (Bearer Token)
- Path Variable: commentId (The ID of the comment to be deleted)
- Response: DefaultMessageResponse



### Join/Leave Community APIs

##### POST /community/join

- Description: Join a community as a member.
- Request Header: Authorization (Bearer Token)
- Request Body: CommunityRequest
- Response: DefaultMessageResponse

##### POST /community/leave

- Description: Leave a community.
- Request Header: Authorization (Bearer Token)
- Request Body: CommunityRequest
- Response: DefaultMessageResponse



### Like/Unlike APIs

##### POST /community/like/post/{postId}

- Description: Like a post.
- Request Header: Authorization (Bearer Token)
- Path Variable: postId (The ID of the post to be liked)
- Response: DefaultMessageResponse

##### POST /community/like/comment/{commentId}

- Description: Like a comment.
- Request Header: Authorization (Bearer Token)
- Path Variable: commentId (The ID of the comment to be liked)
- Response: DefaultMessageResponse

##### POST /community/unlike/post/{postId}

- Description: Unlike a post.
- Request Header: Authorization (Bearer Token)
- Path Variable: postId (The ID of the post to be unliked)
- Response: DefaultMessageResponse

##### POST /community/unlike/comment/{commentId}

- Description: Unlike a comment.
- Request Header: Authorization (Bearer Token)
- Path Variable: commentId (The ID of the comment to be unliked)
- Response: DefaultMessageResponse


## Getting Started

1. Clone the GameBuddy Community Service repository from GitHub.

2. Open the project with your preferred IDE. (Use Gradle.)

3. Configure the necessary database and messaging services (e.g., PostgreSQL).

4. Update the application.yml file with the database credential.

5. Run the application using Gradle or your preferred IDE. (Initial port is 4567. You can change it from application.yml)

## Gradle Commands
To build, test, and run the GameBuddy Community Service, you can use the following Gradle commands:

### Clean And Build
To clean the build artifacts and build the project, run:

`./gradlew clean build`

> The built JAR file will be located in the build/libs/ directory.

### Test
To run the tests for your GameBuddy Community Service, you can use the following Gradle command:

`./gradlew test`

> This command will execute all the unit tests in the project. The test results will be displayed in the console, indicating which tests passed and which ones failed.

Additionally, if you want to generate test reports, you can use the following command:

`./gradlew jacocoTestReport`

> This will generate test reports using the JaCoCo plugin. The test reports can be found in the build/reports/tests and build/reports/jacoco directories. The JaCoCo report will provide code coverage information to see how much of your code is covered by the tests.

### Spotless Code Formatter
This project has Spotless rules. If the code is unformatted, building the project will generate error. To format the code according to the configured Spotless rules, run:

`./gradlew spotlessApply`

### Sonarqube Analysis
To perform a SonarQube analysis of the project, first, ensure you have SonarQube configured and running. Then, run:

`./gradlew sonarqube`

### Run 
To run the GameBuddy Community Service locally using Gradle, use the following command:

`./gradlew bootRun`

> This will start the service, and you can access the APIs at http://localhost:4567.

## Dockerizing the Project
To containerize the GameBuddy Community Service using Docker, follow the steps below:

1. Make sure you have Docker installed on your system. You can download Docker from the official website: https://www.docker.com/get-started

2. Project already has a Dockerfile. Examine the Dockerfile in the root directory of the project. The Dockerfile define the container image configuration.

3. Build the Docker image using the Dockerfile. Open a terminal and navigate to the root directory of the project.

 `docker build -t gamebuddy-community-service .`

 This will create a Docker image with the name **gamebuddy-community-service**.

4. Run the Docker container from the image you just built.

 `docker run -d -p 4567:4567 --name gamebuddy-community gamebuddy-community-service`

 This will start the GameBuddy Community Service container, and it will be accessible at http://localhost:4567.

 
## LICENSE
This project is licensed under the MIT License.

