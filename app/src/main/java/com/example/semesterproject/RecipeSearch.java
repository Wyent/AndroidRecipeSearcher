package com.example.semesterproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeSearch extends AppCompatActivity {
    private RecyclerView mRecyclerView;     // recycler for displaying lists
    private SearchAdapter mAdapter;         // adapter for placing items into recyclerView
    private List<Recipe> mRecipeList;       // list of recipes from api search
    private SearchView mSearchView;         // user input search text
    private Button mHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);    //improves performace if RecyclerView will not change size
        mSearchView = findViewById(R.id.searchView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHome = findViewById(R.id.btnHome);

        Gson gson = new Gson();     //Gson for converting json data to java objects
        //requires dependency implementation in build.gradle (:app)

        OkHttpClient client = new OkHttpClient(); //used for accessing json
        //OkHttpClient requires dependency implementation in build.gradle (:app)


        //performing search
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //builds url to json for recipes
                Request request = new Request.Builder()
                        .url("https://webknox-recipes.p.rapidapi.com/recipes/findByIngredients?ingredients=" + s)
                        .get()
                        .addHeader("x-rapidapi-key", "fed2131e93mshd7f041710699bdap1ce883jsnb3fdafe0e9d1")
                        .addHeader("x-rapidapi-host", "webknox-recipes.p.rapidapi.com")
                        .build();

                //accessing json url
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    //on failure print error
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    } //end onFailure


                    @Override
                    //on success convert json to arraylist of Recipe object
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String myResponse = response.body().string(); //store json as string

                            Type recipeType = new TypeToken<ArrayList<Recipe>>(){}.getType();
                            //only have to do this when the root element of json is an array.
                            //get the Type of an ArrayList of Recipe using TypeToken class.
                            //pass the Type to gson for automatic mapping json to java object.

                            mRecipeList = gson.fromJson(myResponse, recipeType);
                            //converts json string to a list of Recipe objects

                            //todo display an error message
                            //if(mRecipeList.size() == 0)
                                //return; // empty json, no results

                            //must use runOnUiThread when changing UI from inside enqueue
                            //RecipeSearch.this.runOnUiThread(() -> mTextViewResult.setText(mRecipeList.get(0).getName()));
                            mAdapter = new SearchAdapter(RecipeSearch.this, mRecipeList);
                            RecipeSearch.this.runOnUiThread(() -> mRecyclerView.setAdapter(mAdapter));

                            //on recipe click change activities and display recipe data
                            mAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    mRecipeList.get(position);
                                    startActivity(new Intent(RecipeSearch.this, RecipeView.class).putExtra("id", mRecipeList.get(position).getId()));
                                }
                            });

                            //prints the name of the mRecipeList at index 0
                        }
                    } //end onResponse
                }); //end access json url
                return false;
            }//end onQueryTextSubmit

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }//end onQueryTextChange
        }); //end of onQueryTextListener

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecipeSearch.this, MainActivity.class));
            }
        });//End onClick
    }//end of onCreate
}//end RecipeSearch