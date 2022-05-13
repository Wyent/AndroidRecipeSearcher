package com.example.semesterproject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeInfo {
    @SerializedName("extendedIngredients")
    private List<com.example.semesterproject.Ingredients> ingredients;          //arraylist of ingredients
    private String instructions;                    //cooking instructions
    private String image;
    private String title;
    private int id;

    public String getImage() {return image;}

    public String getTitle() {return title;}

    public String getInstructions(){
        return instructions;
    }

    public List<Ingredients> getIngredients(){
        return ingredients;
    }


    public int getId() {
        return id;
    }
}
