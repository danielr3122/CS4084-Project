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
import android.os.AsyncTask;
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

    private StorageReference storageReference;

    //user attributes of new post
    private String userUID;
    private String userNickname;

    //image attribute of new post
    private ImageView imageView;
    private Bitmap imageBitmap;

    //caption of new post
    private TextView captionTxt;

    //used to turn Post object into .json for upload (storage structured as sort of document-based database)
    private Gson gson;

    //location attribute of new post
    private Location currentLocation;
    //used for google play services functionality to determine location
    private FusedLocationProviderClient fusedLocationProviderClient;

    //turns latitude&longitude into address
    Geocoder geocoder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //part of firebase used for storing Post data
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        gson = new Gson();


        //asking for camera permission
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
        // setting up UI elements
        imageView = getView().findViewById(R.id.postImage);
        Button galleryBtn = getView().findViewById(R.id.gallery);
        Button cameraBtn = getView().findViewById(R.id.camera);

        captionTxt = getView().findViewById(R.id.Caption);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
           Switch locationSwitch = getView().findViewById(R.id.locationSwitch);
        locationSwitch.setChecked(false);

        Button uploadBtn = getView().findViewById(R.id.uploadPost);

        //part of firebase where user data is stored
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userUID = currentUser.getUid();

            // Get a reference to the user's data in the Realtime Database
            // this section of code is to get the username so it can later be displayed as part of the post
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

        //checks if switch turned on or off. Either grabs location or sets as null and informs user it has been turned off.
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fetchLastlocation();
                }else{
                    currentLocation = null;
                    Toast.makeText(getContext(),"Location Disabled",Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (imageBitmap == null){//user requires at least an image for a post
                    Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_LONG).show();
                }else {
                    uploadPost();
                }
            }
        });
    }


    //Handles getting image from gallery. Image is converted from Uri to Bitmap
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

    //Handles image selection from camera.
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

    //gets user's location and provides a Toast showing the address of the location the app received
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

    //handles location permission check when location switch is turned on and permission not previously granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 200){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastlocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //Uploads the post to firebase storage by creating an object of class Post and converting it to .json.
    //Each post is assigned a random unique identifier (randomUUID)
    @SuppressLint("StaticFieldLeak")
    private void uploadPost() {
        final String caption = captionTxt.getText() != null ? captionTxt.getText().toString() : "";

        final Post newPost;
        if (currentLocation == null) {
            newPost = new Post(imageBitmap, caption, userUID, userNickname);
        } else {
            newPost = new Post(imageBitmap, caption, currentLocation.getLongitude(), currentLocation.getLatitude(), userUID, userNickname);
        }

        final String jsonNewPost = gson.toJson(newPost);
        final byte[] data = jsonNewPost.getBytes();

        // Informing the user that the upload is taking place
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Post...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        final StorageReference fileRef = storageReference.child("Posts/" + randomKey + ".json");

        //running upload on different thread to aid performance
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... voids) {
                try {
                    fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Uploaded Successfully!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to Upload!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                pd.dismiss();
                if (!success) {
                    Toast.makeText(getContext(), "Failed to Upload!", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}