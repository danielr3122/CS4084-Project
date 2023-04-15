package com.example.cs4084project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("files/Posts");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference prefix : listResult.getPrefixes()) {
                    // all the prefixes under listref
                    // you may call listall() recursively on them
                }
            }
        });

        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setHasFixedSize(true);
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // TODO: pull contacts list, populate adapter, set adapter
//        PostsAdapter postsAdapter = new PostsAdapter()
//        rvPosts.setAdapter(postsAdapter);
        return view;
    }
}