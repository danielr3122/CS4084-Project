package com.example.cs4084project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private byte[] postData;
    private ArrayList<Post> allPosts = new ArrayList<Post>();

    Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Posts");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d("Download Success", "Downloaded Files Sucessfully");
                for(StorageReference item : listResult.getItems()) {
                    item.getBytes(1000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            postData = bytes;
                            String postStringJSON = new String(postData);
                            try {
                                Post currPost = getPostFromJSON(postStringJSON);
                                Log.d("Post Added", currPost.getCaption());
                                allPosts.add(currPost);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Downloading Error", "Failed to download files");
            }
        });

        rvPosts.setHasFixedSize(true);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        PostsAdapter postsAdapter = new PostsAdapter(allPosts);
        rvPosts.setAdapter(postsAdapter);
    }

    private Post getPostFromJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        String encodedImage = jsonObject.getString("imageStr");
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        String decodedCaption = jsonObject.getString("caption");

        Post postFromJSON = new Post(decodedBitmap, decodedCaption);
        return postFromJSON;
    }
}