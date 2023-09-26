package com.nl.recipe.service;

import com.nl.recipe.exception.RecipeExistsException;
import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.repository.RecipeFilterRepository;
import com.nl.recipe.repository.RecipeRepository;
import com.nl.recipe.utils.RecipeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.nl.recipe.utils.RecipeFactory.getSavedRecipe;
import static com.nl.recipe.utils.RecipeFactory.getRecipe;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeFilterRepository recipeFilterRepository;

    @Test
    @DisplayName("addRecipe : GIVEN a recipe THEN saves the recipe in database")
    void addRecipe() {
        when(recipeRepository.existsByRecipeName(anyString())).thenReturn(false);
        when(recipeRepository.save(getRecipe())).thenReturn(getSavedRecipe());
        Recipe recipesList = recipeService.addRecipe(getRecipe());
        assertThat(recipesList.getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @DisplayName("addExistingRecipe : GIVEN a recipe which already exists  THEN returns an Exception with message")
    public void addExistingRecipe() {
        when(recipeRepository.existsByRecipeName(anyString())).thenReturn(true);
        assertThatThrownBy( () -> recipeService.addRecipe(getRecipe()))
                .isInstanceOf(RecipeExistsException.class)
                        .hasMessage("Recipe already exist with Banana Bread");
    }

    @Test
    @DisplayName("getAllRecipes : Fetches all the available recipes in the database")
    void getAllRecipes() {
        List<Recipe> recipeList = Arrays.asList(RecipeFactory.getSavedRecipe());
        when(recipeRepository.findAll()).thenReturn(recipeList);
        List<Recipe> recipesList = recipeService.getAllRecipes();
        assertThat(recipesList.size()).isEqualTo(1);
    }
    @Test
    @DisplayName("updateRecipeById : GIVEN a recipe with latest details THEN updates that in the database")
    void updateRecipeById() {
        Recipe recipe = getSavedRecipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Dry fruits");
        recipe.getIngredients().add(ingredient);
        when(recipeRepository.findById(recipe.getRecipeId())).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        Recipe recipesList = recipeService.updateRecipeById(recipe);
        assertThat(recipesList.getIngredients().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("deleteRecipeById : GIVEN a recipeId  THEN deletes that from the database")
    void deleteRecipeById() {
        Recipe recipe = getSavedRecipe();
        doNothing().when(recipeRepository).deleteById(recipe.getRecipeId());
        recipeService.deleteRecipeById(recipe.getRecipeId());
        verify(recipeRepository, times(1)).deleteById(recipe.getRecipeId());
    }

    @Test
    @DisplayName("searchRecipesByIngredients : GIVEN a few filters THEN returns the recipes matching the filter criteria")
    void searchRecipesByIngredients() {
        RecipesFilterRequest recipesSearchRequest = new RecipesFilterRequest();
        recipesSearchRequest.setCategory("Fish");
        recipesSearchRequest.setServings(1);
        recipesSearchRequest.setInstructions("Oven");
        Map<String, Boolean> map = new HashMap<>();
        map.put("pepper", true);
        recipesSearchRequest.setIngredients(map);
        List<Recipe> recipeList = Arrays.asList(getSavedRecipe());
        when(recipeFilterRepository.filterRecipiesByCriteria(recipesSearchRequest)).thenReturn(recipeList);
        List<Recipe> listRecipes = recipeService.searchRecipesByIngredients(recipesSearchRequest);
        assertEquals(listRecipes.size(), 1);

    }

    @Test
    @DisplayName("getRecipesByCategory : GIVEN a category THEN returns all the recipes under the given category")
    void getRecipesByCategory() {
        List<Recipe> recipeList = Arrays.asList(getSavedRecipe());
        when(recipeRepository.findByCategory(anyString())).thenReturn(Optional.of(recipeList));
        List<Recipe> recipesList = recipeService.getRecipesByCategory("Desert");
        assertEquals(recipesList.size(), 1);
    }
}