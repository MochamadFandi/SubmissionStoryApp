package com.fanindo.submissionstoryapp.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.databinding.ActivityMapsBinding
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.welcome.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    //    private lateinit var dialog: DialogFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupViewModel() {
        mapsViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(UserPreference.getInstance(dataStore), this)
            )[MapsViewModel::class.java]


//        mapsViewModel.isLoading.observe(this) {
//            showLoading(it)
//        }
//
//        mapsViewModel.errorMessage.observe(this) { error ->
//            dialog(this, error)
//        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMapToolbarEnabled = true
            isCompassEnabled = true
        }

        getMyLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showMarker(location)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

        }
    }

    private fun showMarker(location: Location) {
        mapsViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                token = user.token
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        lifecycleScope.launchWhenResumed {
            launch {
                mapsViewModel.getAllStory(token).collect { result ->
                    result.onSuccess { response ->
                        response.listStory.forEach { storyItem ->
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(storyItem.lat, storyItem.lon))
                                    .title(storyItem.name)
                                    .snippet(storyItem.description)
                            )
                        }
                    }
                }
            }
        }

//        mapsViewModel.getAllStory(token).coll(this) { stories ->
//            stories
//            stories.forEach {
//                mMap.addMarker(
//                    MarkerOptions()
//                        .position(LatLng(it.lat, it.lon))
//                        .title(it.name)
//                )
//            }
//
//        }
        val myLocation = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14f))
    }

//    private fun showLoading(state: Boolean) {
//        if (state) {
//            dialog = MyCustomDialog()
//            dialog.show(supportFragmentManager, TAG)
//        } else {
//            dialog.dismiss()
//        }
//    }
//
//    companion object {
//        private const val TAG = "MapsActivity"
//    }


}