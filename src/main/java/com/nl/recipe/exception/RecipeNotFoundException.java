package com.nl.recipe.exception;

/**
 * @author firoz
 * Type RecipeNotFoundException thrown when a user tries to update a nonexisting recipe.
 */
public class RecipeNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public RecipeNotFoundException(String recipeName, String message){
        super(String.format("%s not found with %s",recipeName,message));
    }
    public RecipeNotFoundException(String message){
        super(String.format(message));
    }
}
