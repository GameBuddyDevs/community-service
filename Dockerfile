FROM openjdk:17-jdk-alpine
COPY build/libs/community-service-0.0.1-SNAPSHOT.jar community-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/community-service-0.0.1-SNAPSHOT.jar"]