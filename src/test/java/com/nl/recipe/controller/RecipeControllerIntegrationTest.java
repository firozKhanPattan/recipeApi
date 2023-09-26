package com.nl.recipe.controller;

import com.nl.recipe.model.ErrorDetails;
import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nl.recipe.utils.RecipeFactory.getRecipe;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Type RecipeControllerIntegrationTest Tests the api operations end to end with real inputs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private Integer port;

    private static final String HOST_NAME = "http://localhost:";

    public RecipeControllerIntegrationTest() {
        super();
    }

    @Test
    @Order(1)
    @DisplayName("add Recipe: GIVEN a recipe THEN adds it to the database")
    public void addRecipe() {
        Recipe recipe = getRecipe();
        ResponseEntity<Recipe> recipeResponse = restTemplate.postForEntity(HOST_NAME+port+"/recipe", recipe, Recipe.class);
        assertThat(recipeResponse.getBody().getRecipeId()).isNotNull();
        assertThat(recipeResponse.getBody().getIngredients()).hasSize(3);
        assertThat(recipeResponse.getBody().getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @Order(2)
    @DisplayName("getAllRecipes : Returns all the available recipes")
    public void getAllRecipes() {
        ResponseEntity<List> recipes = restTemplate.getForEntity(HOST_NAME+port+"/recipes", List.class);
        assertThat(recipes.getBody()).isNotEmpty();
        assertThat(recipes.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @Order(3)
    @DisplayName("getRecipesByCategory : GIVEN a category THEN returns all the available recipes under that category")
    public void getRecipesByCategory() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipeByCategory = restTemplate.exchange(HOST_NAME + port + "/recipes?category=Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>(){});
        assertThat(Objects.requireNonNull(recipeByCategory.getBody())).hasSize(1);
        assertThat(recipeByCategory.getBody().get(0).getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @Order(4)
    @DisplayName("updateRecipeById: GIVEN a recipe THEN updates the given recipe")
    public void updateRecipeById() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipes = restTemplate.exchange(HOST_NAME + port + "/recipes?category=Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>(){});
        assertThat(recipes.getBody()).isNotEmpty();
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Dry-fruits");
        recipes.getBody().get(0).getIngredients().add(ingredient);
        assertThat(restTemplate.exchange(HOST_NAME+port+"/recipe",HttpMethod.PUT,  ResponseEntity.ok(recipes.getBody().get(0)), String.class).getBody()).isEqualTo("Recipe updated successfully.");
    }

    @Test
    @Order(5)
    @DisplayName("removeRecipeById : GIVEN a recipeId THEN removes the recipe from the database")
    public void removeRecipeById(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipes = restTemplate.exchange(HOST_NAME + port + "/recipes?category=Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>(){});
        assertThat(recipes.getBody()).isNotEmpty();
        assertThat(restTemplate.exchange(HOST_NAME+port+"/recipe/"+recipes.getBody().get(0).getRecipeId(), HttpMethod.DELETE, null, String.class).getBody()).isEqualTo( "Recipe deleted successfully.");
    }

    @Test
    @Order(6)
    @DisplayName("getRecipeByCategoryForExceptionTest: GIVEN a category and no recipes found under that category THEN returns an exception with message")
    public void getRecipeByCategoryForExceptionTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ErrorDetails> exception = restTemplate.exchange(HOST_NAME + port + "/recipes/category/Fruit", HttpMethod.GET, entity, ErrorDetails.class);
        assertThat(exception.getBody().getMessage()).isEqualTo("No recipes found under :Fruit");
    }

    @Test
    @Order(7)
    @DisplayName("searchRecipesByIngredients : GIVEN a filter criteria THEN returns the recipes that match the filter criteria")
    public void searchRecipesByIngredients() {
        RecipesFilterRequest recipesFilterRequest= new RecipesFilterRequest();
        recipesFilterRequest.setCategory("Fish");
        recipesFilterRequest.setServings(1);
        recipesFilterRequest.setInstructions("Oven");
        Map<String, Boolean> map = new HashMap<>();
        map.put("pepper", true);
        recipesFilterRequest.setIngredients(map);
        ResponseEntity<List> recipes = restTemplate.postForEntity(HOST_NAME  + port + "/search/recipes", recipesFilterRequest, List.class);
        assertThat(recipes.getStatusCodeValue()).isEqualTo(200);
    }
}
