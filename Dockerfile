FROM openjdk:17-jdk-slim
COPY target/universitycms-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "universitycms-0.0.1-SNAPSHOT.jar"]
