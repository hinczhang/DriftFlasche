package org.standardserve.driftflasche

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import org.standardserve.driftflasche.PermissionUtil.PermissionDeniedDialog.Companion.newInstance
import org.standardserve.driftflasche.PermissionUtil.isPermissionGranted
import org.standardserve.driftflasche.databinding.ActivityMapsBinding
import org.standardserve.driftflasche.dialog.MarkerCreationDialog

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    LocationSource.OnLocationChangedListener, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userName_sidebar: TextView
    private var permissionDenied = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottleButton: Button

    private lateinit var token: String
    private lateinit var username: String
    private lateinit var loginIntet : Intent

    private var firstBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginIntet = Intent(this, LoginActivity::class.java)
        token = intent.getStringExtra("token").toString()
        username = intent.getStringExtra("username").toString()
        permissionInit()
        componentInit()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun componentInit(){
        userName_sidebar = findViewById<NavigationView>(R.id.nav_view).inflateHeaderView(R.layout.sidebar_headerlayout).findViewById(R.id.username)
        userName_sidebar.text = username
        bottleButton = findViewById(R.id.bottleButton)
        bottleButton.setOnClickListener {
            /*fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                override fun isCancellationRequested() = false
            }).addOnSuccessListener { location: Location? ->
                if (location == null){
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                }
                else {
                    val lat = location.latitude
                    val lon = location.longitude
                    val furtherPoint = LatLng(lat, lon)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(17F));
                    var marker = mMap.addMarker(MarkerOptions().position(furtherPoint).title("Current Marker").icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.info_driftbottle)))
                    //Toast.makeText(this, "location: $lat, $lon", Toast.LENGTH_SHORT).show()
                }

            }*/
            MarkerCreationDialog.create(this)

        }
    }

    private fun permissionInit(){
        OverallPermission.enableExternalStoragePermission(this)
    }

    /*
    * Define a turn back request
    * */
    override fun onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            firstBackTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
            System.exit(0)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        enableLocalization()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        }).addOnSuccessListener { location: Location? ->
            if (location == null){
                Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
            }
            else {
                val lat = location.latitude
                val lon = location.longitude
                val furtherPoint = LatLng(lat, lon)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
                Toast.makeText(this, "location: $lat, $lon", Toast.LENGTH_SHORT).show()
            }

        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener { location: Location? ->
                if (location == null){
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                }
                else {
                    val lat = location.latitude
                    val lon = location.longitude
                    val furtherPoint = LatLng(lat, lon)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15F));
                    Toast.makeText(this, "location: $lat, $lon", Toast.LENGTH_SHORT).show()
                } }
    }

    override fun onLocationChanged(p0: Location) {
        return
    }

    override fun onMyLocationClick(p0: Location) {
        return
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }


    /*
    *  Enable the localization permission
    *  Grant the corresponding permission to start the program
    * */
    @SuppressLint("MissingPermission")
    private fun enableLocalization(){
        // 1. check if permissions are granted, if so, enable the location layer
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
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
            PermissionUtil.RationaleDialog.newInstance(
                OverallPermission.LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            OverallPermission.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ){
        if (requestCode != OverallPermission.LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableLocalization()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }
}