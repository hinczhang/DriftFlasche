package org.standardserve.driftflasche;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.navigation.NavigationView;
import org.standardserve.driftflasche.databinding.ActivityMapsBinding;
import org.standardserve.driftflasche.dialog.MarkerCreationDialog;
import org.standardserve.driftflasche.dialog.bottlesReload;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    LocationSource.OnLocationChangedListener, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView userName_sidebar;
    private boolean permissionDenied = false;
    private FusedLocationProviderClient fusedLocationClient;
    private Button bottleButton;

    private String token;
    private String username;
    private Intent loginIntent;

    private float firstBackTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginIntent = getIntent();
        token = loginIntent.getStringExtra("token");
        username = loginIntent.getStringExtra("username");
        permissionInit();
        componentInit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void componentInit(){

        userName_sidebar = ((NavigationView)findViewById(R.id.nav_view)).inflateHeaderView(R.layout.sidebar_headerlayout).findViewById(R.id.username);
        userName_sidebar.setText(username);
        bottleButton = findViewById(R.id.bottleButton);
        bottleButton.setOnClickListener(v -> {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            }).addOnSuccessListener(location -> {
                MarkerCreationDialog.create(this, username, location.getLatitude(), location.getLongitude(), token);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17F));
            });
        });
    }

    private void permissionInit(){
        OverallPermission.INSTANCE.enableExternalStoragePermission(this);
    }

    /*
    * Define a turn back request
    * */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            System.exit(0);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        enableLocalization();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(location -> {
            if (location == null){
                Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show();
            }else{
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                LatLng furtherPoint = new LatLng(lat, lon);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15F));
                bottlesReload.loadBottlesbyDistance(this, 20, lat, lon, token, username, mMap);
                Toast.makeText(this, "location: $lat, $lon", Toast.LENGTH_SHORT).show();
            }
        });


        fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(
                                location -> {
                                    if (location != null) {
                                        double lat = location.getLatitude();
                                        double lon = location.getLongitude();
                                        LatLng furtherPoint = new LatLng(lat, lon);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint));
                                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15F));
                                        Toast.makeText(this, "location: $lat, $lon", Toast.LENGTH_SHORT).show();
                                    }
                                });
    }


    /*
    *  Enable the localization permission
    *  Grant the corresponding permission to start the program
    * */
    @SuppressLint("MissingPermission")
    private void enableLocalization(){
        // 1. check if permissions are granted, if so, enable the location layer
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.setMyLocationEnabled(true);
            return;
        }

        //2. If not, then a permission rationale dialog should be shown
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {

            PermissionUtil.RationaleDialog.Companion.newInstance(
                OverallPermission.LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(getSupportFragmentManager(), "dialog");
            return;
        }

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,permissions, OverallPermission.LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults
    ){
        if (requestCode != OverallPermission.LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            );
            return;
        }

        if (PermissionUtil.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || PermissionUtil.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableLocalization();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtil.PermissionDeniedDialog.Companion.newInstance(true).show(
            getSupportFragmentManager(), "dialog"
        );
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

}