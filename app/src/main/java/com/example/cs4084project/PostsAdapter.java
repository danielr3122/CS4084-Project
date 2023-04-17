package com.example.cs4084project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView captionText;
        public ImageView imageView;
        public Button locationButton;

        public ViewHolder(View itemView){
            super(itemView);
            captionText = itemView.findViewById(R.id.post_caption);
            imageView = itemView.findViewById(R.id.post_image);
            locationButton = itemView.findViewById(R.id.location_button);
        }
    }

    private ArrayList<Post> allPosts;

    public PostsAdapter(ArrayList<Post> allPosts){
        this.allPosts = allPosts;
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder holder, int position){
        Post post = allPosts.get(position);
        TextView textView = holder.captionText;
        textView.setText(post.getCaption());
        ImageView imageView = holder.imageView;
        imageView.setImageBitmap(post.getImage());
        Button locationBtn = holder.locationButton;
        locationBtn.setOnClickListener(view -> showMapsFragment(post.getLatitude(), post.getLongitude(), view));
    }

    @Override
    public int getItemCount(){
        return allPosts.size();
    }

    public void showMapsFragment(double latitude, double longitude, View view){
        Fragment mapsFragment = new MapsFragment(latitude, longitude);
        ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.content, mapsFragment).addToBackStack(null).commit();
    }
}
