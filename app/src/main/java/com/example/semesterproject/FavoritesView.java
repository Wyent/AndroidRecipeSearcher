package com.example.semesterproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavoritesView extends AppCompatActivity {    private RecyclerView mRecyclerView;     // recycler for displaying lists
    private SearchAdapter mAdapter;         // adapter for placing items into recyclerView
    public List<Recipe> mFavoriteList;       // list of recipes from api search
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private List<String> mFavoritesIDs;
    private Button mHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_view);
        mFavoritesIDs = new ArrayList<>();
        mHome = findViewById(R.id.btnHome);
        mRecyclerView = findViewById(R.id.recyclerView);
        //mRecyclerView.setHasFixedSize(true);    //improves performace if RecyclerView will not change size
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Gson gson = new Gson();     //Gson for converting json data to java objects
        mFavoriteList = new ArrayList<>();

        //requires dependency implementation in build.gradle (:app)
        OkHttpClient client = new OkHttpClient(); //used for accessing json
        //OkHttpClient requires dependency implementation in build.gradle (:app)

        //get a list of id's from firebase

        mFirestore.collection("users").document(mAuth.getUid()).collection("favorites")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (int i = 0; i < document.getDocuments().size(); i++) {
                        DocumentSnapshot doc = document.getDocuments().get(i);
                        String id = doc.get("id").toString();
                        mFavoritesIDs.add(id);
                    }

                    //build link to search api by ID
                    for(int i = 0; i < mFavoritesIDs.size(); i++) {
                        Request request = new Request.Builder()
                                .url("https://webknox-recipes.p.rapidapi.com/recipes/" + mFavoritesIDs.get(i) + "/information")
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
                                if (response.isSuccessful()) {
                                    String myResponse = response.body().string(); //store json as string

                                    mFavoriteList.add(gson.fromJson(myResponse, Recipe.class));
                                    //converts json string to recipeInfo objects
                                    //which contains cooking instructions
                                    //and an arraylist of ingredients
                                    //must use runOnUiThread when changing UI from inside enqueue

                                    mAdapter = new SearchAdapter(FavoritesView.this, mFavoriteList);
                                    FavoritesView.this.runOnUiThread(() -> mRecyclerView.setAdapter(mAdapter)); // will crash without runOnUiThread when going back and forth

                                    //on recipe click change activities and display recipe data
                                    mAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            mFavoriteList.get(position);
                                            startActivity(new Intent(FavoritesView.this, RecipeView.class).putExtra("id", mFavoriteList.get(position).getId()));
                                        }
                                    });

                                    //prints the name of the mRecipeList at index 0
                                }
                            } //end onResponse
                        }); //end access json url

                    }

                }

            }
        });

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoritesView.this, MainActivity.class));
            }
        });
    }
}