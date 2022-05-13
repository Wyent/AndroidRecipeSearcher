package com.example.semesterproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Recipe> recipeSearchResults;  // list of recipes to be used in RecyclerView
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SearchAdapter(Context context, List<Recipe> recipeSearchResults) {
        this.layoutInflater = LayoutInflater.from(context);
        this.recipeSearchResults = recipeSearchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_view, parent, false);
        //View view = layoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Recipe currentItem = recipeSearchResults.get(position);
        // load images and text into recycler elements
        holder.textName.setText(currentItem.getName());
        holder.textLikes.setText("Likes: " + currentItem.getLikes());
        Picasso.get().load(currentItem.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return recipeSearchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textName, textLikes;
        public ImageView image;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) { // constructor
            super(itemView);
            textName = itemView.findViewById(R.id.recipeName);
            textLikes = itemView.findViewById(R.id.recipeLikes);
            image = itemView.findViewById(R.id.recipeImage);

            itemView.setOnClickListener(new View.OnClickListener() { //clicking items in recipe search
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();    // get adapter position
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);     // pass position to interface
                        }
                    }
                }
            });
        }
    }
}
