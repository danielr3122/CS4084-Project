package com.example.cs4084project;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    private byte[] postData;
    private ArrayList<Post> allPosts = new ArrayList<>();
    private int numOfPosts;
    private int currPostNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("Posts");

        // Displays loading bar while posts load in
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Refreshing Feed...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        // Downloads all posts from Firebase Storage
        storageReference.listAll().addOnSuccessListener(listResult -> {
            numOfPosts = listResult.getItems().size();

            // Ends loading if there are no posts
            if(numOfPosts == 0){
                progressDialog.dismiss();
            }

            // Loops through each post and adds it to an arraylist
            // Once all posts have been read, it connects the adapter
            // to the recyclerview, which displays all posts.
            for(StorageReference item : listResult.getItems()) {
                item.getBytes(1000000).addOnSuccessListener(bytes -> {
                    postData = bytes;
                    String postStringJSON = new String(postData);
                    try {
                        Post currPost = getPostFromJSON(postStringJSON);
                        allPosts.add(currPost);
                        currPostNum++;

                        if(currPostNum == numOfPosts){
                            currPostNum = 0;
                            rvPosts = view.findViewById(R.id.rvPosts);
                            progressDialog.dismiss();
                            rvPosts.setHasFixedSize(true);
                            rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
                            PostsAdapter postsAdapter = new PostsAdapter(allPosts, getContext());
                            rvPosts.setAdapter(postsAdapter);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e("Downloading Error", "Failed to download files"));
    }

    // Function used to decode Posts from their JSON form
    private Post getPostFromJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String encodedImage = jsonObject.getString("imageStr");
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String decodedCaption = jsonObject.getString("caption");

        if(jsonObject.getBoolean("containsLocation")){
            double decodedLongitude = jsonObject.getDouble("longitude");
            double decodedLatitude = jsonObject.getDouble("latitude");
            return new Post(decodedBitmap, decodedCaption, decodedLongitude, decodedLatitude);
        }
        return new Post(decodedBitmap, decodedCaption);
    }
}