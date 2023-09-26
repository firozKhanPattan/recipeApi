# recipeApi
RecipeApi

An api to mamage the recies. A usercan add a new recipe, update the existing recipe, search all the available recipes, delete a recipe, search recipes on a filter criteria.
This API has been developed usin Java 17, Spring-boot 3.1.4 and Maven.

All the entities are mapped using the JPA.  
Used Docker composer to run api in the docker container.
Used h2 inmemory h2 database.

Instructions to run to run this api:

Build : mvn clean install
Run: docker-compose up
Prerequisite: Please make sure that docker desk top is running.

Health check using Spring actuator url : http://localhost:8080/actuator/health

Swagger-ui url : http://localhost:8080/swagger-ui/index.html

Please refer to the postman collection file for the sample requests.


