package com.example.bitamirshafiee.ml_kit_skeleton.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.bitamirshafiee.ml_kit_skeleton.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/*
This class it contains the GPS data in reporting user's location
and checks the permission to access location.
It sets the therapy centers in Kuwait.
 */
class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@LocationActivity)
        fetchLocation()
    }


    /*
    fetchLocation() includes GPS data in reporting user's location.
    Also, to check if the user has already granted the app a particular permission,
    that permission is passed into the ContextCompat.checkSelfPermission() method.
    This method returns either PERMISSION_GRANTED or PERMISSION_DENIED,
    depending on whether the app has the permission.
     */
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
//                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
//                        currentLocation.longitude, Toast.LENGTH_SHORT).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.myMap) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@LocationActivity)
            } }
    }

//    it is triggered when the map is ready to be used
//    and provides a non-null instance of GoogleMap.
    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.addMarker(markerOptions)

        setLocations(googleMap)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] ==
        PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@LocationActivity, AnalyticsActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this@LocationActivity, AnalyticsActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // setLocations sets the therapy centers in Kuwait
    private fun setLocations(googleMap: GoogleMap?) {
        val latLng1 = LatLng(29.3612362,47.9685744)
        val markerOptions1 = MarkerOptions().position(latLng1).title("Soor Center for Professional Therapy & Assessment")
        googleMap?.addMarker(markerOptions1)

        val latLng2 = LatLng(29.3835400, 47.9887266)
        val markerOptions2 = MarkerOptions().position(latLng2).title("Let’s speech")
        googleMap?.addMarker(markerOptions2)

        val latLng3 = LatLng(29.3791545, 47.9934599)
        val markerOptions3 = MarkerOptions().position(latLng3).title("Kuwait counseling center")
        googleMap?.addMarker(markerOptions3)

        val latLng4 = LatLng(29.3702049, 47.9929548)
        val markerOptions4 = MarkerOptions().position(latLng4).title("Al-Suwaidan Clinic")
        googleMap?.addMarker(markerOptions4)

        val latLng5 = LatLng(29.3307337, 48.0429199)
        val markerOptions5 = MarkerOptions().position(latLng5).title("مركز نمو للاستشارات النفسية")
        googleMap?.addMarker(markerOptions5)

        val latLng6 = LatLng(29.3393967, 48.0435968)
        val markerOptions6 = MarkerOptions().position(latLng6).title("د. فيصل العجمي للاستشارات النفسية")
        googleMap?.addMarker(markerOptions6)

        val latLng7 = LatLng(29.3227228, 48.0151389)
        val markerOptions7 = MarkerOptions().position(latLng7).title("Dr. Mariam Al Awadhi Mental Health Clinic")
        googleMap?.addMarker(markerOptions7)

        val latLng8 = LatLng(29.3330674, 48.0828720)
        val markerOptions8 = MarkerOptions().position(latLng8).title("American Center for Physiotherapy")
        googleMap?.addMarker(markerOptions8)

        val latLng9 = LatLng(29.0827553, 48.1398224)
        val markerOptions9 = MarkerOptions().position(latLng9).title("مركز السمو للاستشارات النفسية والاجتماعية")
        googleMap?.addMarker(markerOptions9)

        val latLng10 = LatLng(29.3380523, 48.0550686)
        val markerOptions10 = MarkerOptions().position(latLng10).title("Dr. Abdullah Gholoum Clinic")
        googleMap?.addMarker(markerOptions10)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng10))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng10, 10f))



    }

}