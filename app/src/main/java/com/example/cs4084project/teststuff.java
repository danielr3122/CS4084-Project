package com.example.cs4084project;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private LatLng latLng;

    private TextView textView;
    private EditText editTextLat;
    private EditText editTextLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        textView = findViewById(R.id.textView);
        editTextLat = findViewById(R.id.editTextLat);
        editTextLong = findViewById(R.id.editTextong);

        latLng = new LatLng(-34, 151);
    }

    public void getLocationDetails(View view){
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        if (!(editTextLong.getText().toString().isEmpty() || editTextLat.getText().toString().isEmpty()))
        {
            latitude = Double.parseDouble(editTextLat.getText().toString());
            longitude = Double.parseDouble(editTextLong.getText().toString());
            latLng = new LatLng(latitude, longitude);
        }
        Geocoder geocoder;
        List(Address) addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String address = null;
        String city = null;
        String state = null;
        String country = null;
        String postalCode = null;
        String knonName = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knonName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in : " + address + city + state + country + postalCode + knonName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        textView.setText(address + city + state + country + postalCode + knonName);
    }
}