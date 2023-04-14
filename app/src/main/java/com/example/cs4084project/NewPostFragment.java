package com.example.cs4084project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    ImageView imageView;
    Uri imageUri;
    Bitmap imageBitmap;
    Button galleryBtn;
    Button cameraBtn;

    TextView captionTxt;
    String caption;

    Gson gson;

    Button uploadBtn;

    public NewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        gson = new Gson();

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
        ) {
            String[] permission = {
                    android.Manifest.permission.CAMERA
            };
            requestPermissions(permission, 100);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        imageView = getView().findViewById(R.id.postImage);
        galleryBtn =  getView().findViewById(R.id.gallery);
        cameraBtn =  getView().findViewById(R.id.camera);

        captionTxt = (TextView) getView().findViewById(R.id.Caption);

        uploadBtn =  getView().findViewById(R.id.uploadPost);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent =
                        new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 110);
            }

        });


        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (imageUri == null){
                    Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_LONG).show();
                }else {
                    uploadPicture();
                }
            }
        });
    }


    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == DashboardActivity.RESULT_OK) {
                        imageUri = result.getData().getData();

                        try {
                            InputStream inputStream = getActivity().
                                    getApplicationContext().getContentResolver().openInputStream(imageUri);

                            imageBitmap = BitmapFactory.decodeStream(inputStream);

                            imageView.setImageBitmap(imageBitmap);

                        }catch (FileNotFoundException e){

                        }
                    }
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 110){
            if(resultCode == DashboardActivity.RESULT_OK) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(imageBitmap);

            }
        }

    }


    private void uploadPicture() {

        if (captionTxt.getText() != null) {
            caption = captionTxt.getText().toString();
        } else caption = "";

        Post newPost = new Post(imageBitmap, caption);
        String jsonNewPost = gson.toJson(newPost);
        byte[] data = jsonNewPost.getBytes();

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Post...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference fileRef = storageReference.child("Posts/" + randomKey + ".json");


        UploadTask uploadTask = fileRef.putBytes(data);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Uploaded Successfully!", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Upload!", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                });
    };




    /*  To use the method getPostFromJSON()


        Post testPost;
        try {
            testPost = getPostFromJSON(jsonNewPost);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        */
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

