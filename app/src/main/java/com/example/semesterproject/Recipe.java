package com.example.semesterproject;

import com.google.gson.annotations.SerializedName;

public class Recipe {
    private int id;                 //id# of recipe
    private int likes;               //# of likes of the recipe
    @SerializedName("title")
    private String name;            //name of the recipe
    private String image;           //image url of recipe

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getImage(){
        return image;
    }

    public Recipe(String name, String image, int likes, int id) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.likes = likes;
    }

    public Recipe(int id) { //constructor with just ID from firebase favorites
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }
}
