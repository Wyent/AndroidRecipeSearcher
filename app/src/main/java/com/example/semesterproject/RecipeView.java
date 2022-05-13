package com.example.semesterproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RecipeView extends AppCompatActivity {

    private TextView mName, mInstructions;
    private ImageView mImage;
    private RecyclerView mRecycler;
    private IngredientAdapter adapter;
    private Button mFavorite;
    private int id;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Boolean favorited;
    private Button mHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        mFirestore = FirebaseFirestore.getInstance();
        mInstructions = findViewById(R.id.txtvInstructions);
        mName = findViewById(R.id.txtvRecipeName);
        mImage = findViewById(R.id.imgRecipePicture);
        mFavorite= findViewById(R.id.btnFavorite);
        mRecycler = findViewById(R.id.recyclerIngredients);
        mHome = findViewById(R.id.btnHome);
        mRecycler.setHasFixedSize(true);    //improves performace if RecyclerView will not change size
        mAuth = FirebaseAuth.getInstance();
        mRecycler.setLayoutManager(new LinearLayoutManager(RecipeView.this));

        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient(); //used for accessing json
        //OkHttpClient requires dependency implementation in build.gradle (:app)

        Intent mIntent = getIntent();
        if(mIntent.getExtras() != null)
            id = mIntent.getIntExtra("id", 0);

        DocumentReference favRef = mFirestore.collection("users").document(mAuth.getUid()).collection("favorites").document(Integer.toString(id));
        favRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    favorited = true;
                    mFavorite.setText("Unfavorite Recipe");
                }
                else {
                    favorited = false;
                    mFavorite.setText("Favorite Recipe");
                }
            }
        });


        //builds url to json for ingredients and instructions
        Request request = new Request.Builder()
                .url("https://webknox-recipes.p.rapidapi.com/recipes/" + id + "/information")
                .get()
                .addHeader("x-rapidapi-key", "fed2131e93mshd7f041710699bdap1ce883jsnb3fdafe0e9d1")
                .addHeader("x-rapidapi-host", "webknox-recipes.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            //on failure print error
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }//end onFailure

            @Override
            //on success convert json to instructions and arraylist ingredients of RecipeInfo object
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String myResponse = response.body().string(); //store json as string

                    RecipeInfo recipeInfo = gson.fromJson(myResponse, RecipeInfo.class);
                    //converts json string to recipeInfo objects
                    //which contains cooking instructions
                    //and an arraylist of ingredients
                    //must use runOnUiThread when changing UI from inside enqueue
                    RecipeView.this.runOnUiThread(() -> mName.setText(recipeInfo.getTitle()));
                    RecipeView.this.runOnUiThread(() -> mInstructions.setText(recipeInfo.getInstructions()));
                    RecipeView.this.runOnUiThread(() -> Picasso.get().load(recipeInfo.getImage()).into(mImage)); //latest version of picasso replaced .with with .get
                    List<Ingredients> ingredients = recipeInfo.getIngredients();
                    adapter = new IngredientAdapter(RecipeView.this, ingredients);
                    RecipeView.this.runOnUiThread(() -> mRecycler.setAdapter(adapter)); // will crash without runOnUiThread when going back and forth
                }// end if
            }//end onResponse
        });//end accessing json url

        //End OnClick
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            DocumentReference docRef = mFirestore.collection("users").document(mAuth.getUid()).collection("favorites").document(String.valueOf(id));

            if (favorited) {
                 docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                      public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()) {
                             Toast.makeText(RecipeView.this, "Recipe Unfavorited", Toast.LENGTH_SHORT);
                             favorited = false;
                                mFavorite.setText("Favorite Recipe");
                         }
                            else {
                                Toast.makeText(RecipeView.this, "Failed to unfavorite recipe", Toast.LENGTH_SHORT);
                            }
                     }
                 });
               }
                else {
                    Map<String, Object> favorite = new HashMap<>();
                    favorite.put("id", id);
                    docRef.set(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                             Toast.makeText(RecipeView.this, "Recipe Favorited", Toast.LENGTH_SHORT);
                                favorited = true;
                                mFavorite.setText("Unfavorite Recipe");
                                //startActivity(new Intent(RecipeView.this, RecipeView.class).putExtra("id", id));
                            }
                            else {
                                Toast.makeText(RecipeView.this, "Failed to favorite recipe", Toast.LENGTH_SHORT);
                            }
                         }
                     });
                }
            }
        });

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecipeView.this, MainActivity.class));
            }
        });
    }//End OnCreate
}//End RecipeView