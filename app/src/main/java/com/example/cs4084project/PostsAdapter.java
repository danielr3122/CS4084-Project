package com.example.cs4084project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public Context prevContext;

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

    public PostsAdapter(ArrayList<Post> allPosts, Context prevContext){
        this.allPosts = allPosts;
        this.prevContext = prevContext;
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
        ImageView imageView = holder.imageView;
        Button locationBtn = holder.locationButton;
        imageView.setImageBitmap(post.getImage());
        textView.setText(post.getCaption());

        if(!post.hasLocation()){
            locationBtn.setVisibility(View.GONE);
        } else {
            locationBtn.setVisibility(View.VISIBLE);
        }
        locationBtn.setOnClickListener(view -> showMapsFragment(view, post.getLatitude(), post.getLongitude()));
    }

    @Override
    public int getItemCount(){
        return allPosts.size();
    }

    public void showMapsFragment(View view, double latitude, double longitude){
        MapsFragment fragment5 = new MapsFragment(latitude, longitude);
        FragmentTransaction fragmentTransaction = ((AppCompatActivity)prevContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment5, "");
        fragmentTransaction.addToBackStack("Return to Home Screen");
        fragmentTransaction.commit();
    }
}
