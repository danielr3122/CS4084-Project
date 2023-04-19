package com.example.cs4084project;

import android.Manifest;
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
import android.widget.CheckBox;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
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

    private ImageView imageView;
    private Bitmap imageBitmap;

    private TextView captionTxt;

    private Gson gson;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private CheckBox locationCheckBox;
    private MutableLiveData<Boolean> locationReceived = new MutableLiveData<>();

    private boolean postCreated;

    Geocoder geocoder;

    public NewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        geocoder = new Geocoder(getContext(), Locale.getDefault());

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
        postCreated = false;
        imageView = view.findViewById(R.id.postImage);
        Button galleryBtn = view.findViewById(R.id.gallery);
        Button cameraBtn = view.findViewById(R.id.camera);

        captionTxt = getView().findViewById(R.id.Caption);

        locationCheckBox = view.findViewById(R.id.location_checkbox);

        Button uploadBtn = view.findViewById(R.id.uploadPost);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        galleryBtn.setOnClickListener(v -> {
            Intent galleryIntent =
                    new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivityResultLauncher.launch(galleryIntent);
        });

        cameraBtn.setOnClickListener(v -> {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 110);
        });

        uploadBtn.setOnClickListener(v -> {
            if (imageBitmap == null){
                Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_LONG).show();
            } else {
                if(locationCheckBox.isChecked()){
                    locationReceived.setValue(false);
                    fetchLastLocation();
                    locationReceived.observe(getViewLifecycleOwner(), aBoolean -> {
                        if(!postCreated && aBoolean){
                            uploadPicture();
                            postCreated = true;
                        }
                    });
                } else {
                    if(!postCreated){
                        uploadPicture();
                        postCreated = true;
                    }
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
                            InputStream inputStream = getActivity().
                                    getApplicationContext().getContentResolver().openInputStream(imageUri);

                            imageBitmap = BitmapFactory.decodeStream(inputStream);

                            imageView.setImageBitmap(imageBitmap);

                        } catch (FileNotFoundException e){

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

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            locationReceived.setValue(true);
            return;
        }

        Task task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(o -> {
            Location location = (Location) o;
            if (location != null){
                currentLocation = location;
            }

            try {
                List<Address> listAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                Toast.makeText(getContext(), "Address used: " +listAddress.get(0).getAddressLine(0)+", "+ listAddress.get(0).getCountryName(), Toast.LENGTH_LONG).show();
                locationReceived.setValue(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            if (requestCode == 200){
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
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
            newPost = new Post(imageBitmap, caption);
        }else{
            newPost = new Post(imageBitmap, caption, currentLocation.getLongitude(), currentLocation.getLatitude());
        }
        String jsonNewPost = gson.toJson(newPost);

        byte[] data = jsonNewPost.getBytes();
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Post...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference fileRef = storageReference.child("Posts/" + randomKey + ".json");


        UploadTask uploadTask = fileRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getContext(), "Uploaded Successfully!", Toast.LENGTH_LONG).show();
            pd.dismiss();
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to Upload!", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                });

        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_post));
        captionTxt.setText(null);
        locationCheckBox.setChecked(false);
    }
}

