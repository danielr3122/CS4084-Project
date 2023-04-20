package com.example.cs4084project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NewPostFragment extends Fragment {

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String userUID;
    private String userNickname;

    private ImageView imageView;
    private Bitmap imageBitmap;

    private TextView captionTxt;

    private Gson gson;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Geocoder geocoder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        gson = new Gson();

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) ==
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        imageView = getView().findViewById(R.id.postImage);
        Button galleryBtn = getView().findViewById(R.id.gallery);
        Button cameraBtn = getView().findViewById(R.id.camera);

        captionTxt = getView().findViewById(R.id.Caption);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch locationSwitch = getView().findViewById(R.id.locationSwitch);
        locationSwitch.setChecked(false);

        Button uploadBtn = getView().findViewById(R.id.uploadPost);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userUID = currentUser.getUid();

            // Get a reference to the user's data in the Realtime Database
            DatabaseReference userRef = mDatabase.child("users").child(userUID);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Retrieve the user's data from the database
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    userNickname = user.getNickname();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileFragment", "Error retrieving user data from Realtime Database: " + error.getMessage());
                }
            });
        } else {
            Log.e("ProfileFragment", "Current user is null.");
        }


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

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fetchLastlocation();
                }else{
                    currentLocation = null;
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (imageBitmap == null){
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
                        Uri imageUri = result.getData().getData();

                        try {
                            InputStream inputStream = requireActivity().
                                    getApplicationContext().getContentResolver().openInputStream(imageUri);

                            imageBitmap = BitmapFactory.decodeStream(inputStream);

                            imageView.setImageBitmap(imageBitmap);

                        }catch (FileNotFoundException e){
                            Log.i("FNF","File not found exception in gallery image select");
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

    private void fetchLastlocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Location location = (Location) o;
                if (location != null){
                    currentLocation = location;
                }

                try {
                    List<Address> listAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    Toast.makeText(getContext(), "Address used: " +listAddress.get(0).getAddressLine(0)+", "+ listAddress.get(0).getCountryName(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 200){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastlocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void uploadPicture() {

        String caption;
        if (captionTxt.getText() != null) {
            caption = captionTxt.getText().toString();
        } else caption = "";

        Post newPost;

        if(currentLocation == null) {
            newPost = new Post(imageBitmap, caption, userUID, userNickname);
        }else{
            newPost = new Post(imageBitmap, caption, currentLocation.getLongitude(), currentLocation.getLatitude(), userUID, userNickname);
        }


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


    }
}