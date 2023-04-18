package com.example.cs4084project;

import android.app.ProgressDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private byte[] postData;
    private ArrayList<Post> allPosts = new ArrayList<Post>();
    private int numOfPosts;
    private int currPostNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Posts");

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Refreshing Feed");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                numOfPosts = listResult.getItems().size();
                for(StorageReference item : listResult.getItems()) {
                    item.getBytes(1000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            postData = bytes;
                            String postStringJSON = new String(postData);
                            try {
                                Post currPost = getPostFromJSON(postStringJSON);
                                allPosts.add(currPost);
                                currPostNum++;
                                progressDialog.setProgress((100/numOfPosts) * currPostNum);

                                if(currPostNum == numOfPosts){
                                    currPostNum = 0;
                                    rvPosts = view.findViewById(R.id.rvPosts);
                                    progressDialog.setProgress(100);
                                    progressDialog.dismiss();
                                    rvPosts.setHasFixedSize(true);
                                    rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
                                    PostsAdapter postsAdapter = new PostsAdapter(allPosts, getContext());
                                    rvPosts.setAdapter(postsAdapter);
                                }
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
    }

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