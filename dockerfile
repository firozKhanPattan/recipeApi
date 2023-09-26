FROM openjdk:17-alpine
VOLUME /tmp
EXPOSE 8080
ADD target/recipeApi-0.0.1-SNAPSHOT.jar recipeApi-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","recipeApi-0.0.1-SNAPSHOT.jar"]