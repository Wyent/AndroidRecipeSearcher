package com.example.semesterproject;

import com.google.gson.annotations.SerializedName;

public class Ingredients {
    private int id;                         //id# of the ingredient
    @SerializedName("original")
    private String name;                    //name of the ingredient
    @SerializedName("originalString")
    private String amountText;              //portion of the ingredient

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getAmountText(){
        return amountText;
    }
}


