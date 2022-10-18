package com.fanindo.submissionstoryapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.viewpager2.widget.ViewPager2
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.databinding.ActivityHomeBinding
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.map.MapsActivity
import com.fanindo.submissionstoryapp.ui.newstory.NewStoryActivity
import com.fanindo.submissionstoryapp.ui.welcome.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity()
//    ,OnMapReadyCallback
{

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    //    private lateinit var dialog: DialogFragment
    private lateinit var handler: Handler

    //    private lateinit var mMap: GoogleMap
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var doubleBackToExitPressedOnce = false
    private var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
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
        handler = Handler(Looper.myLooper()!!)
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupViewModel() {
        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(UserPreference.getInstance(dataStore), this)
            )[HomeViewModel::class.java]

        homeViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvName.text = getString(R.string.halo_name, user.name)
                token = user.token
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        homeViewModel.getAllStory(token).observe(this) { stories ->
            if (stories == null) {
                binding.tvEmptyStory.visibility = View.VISIBLE
            } else {
                binding.tvEmptyStory.visibility = View.INVISIBLE
                setStoryData(stories)
            }
        }

//        homeViewModel.isLoading.observe(this) {
//            showLoading(it)
//        }
//
//        homeViewModel.errorMessage.observe(this) { error ->
//            dialog(this, error)
//        }
    }

    private fun setupAction() {
        binding.ivLogout.setOnClickListener {
            homeViewModel.logout()
        }

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, NewStoryActivity::class.java))
        }

        binding.tvViewMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun setStoryData(stories: PagingData<Story>) {

        val adapter = StoryAdapter()

        adapter.submitData(lifecycle, stories)
        binding.rvStory.adapter = adapter
        binding.rvStory.clipToPadding = false
        binding.rvStory.clipChildren = false
        binding.rvStory.offscreenPageLimit = 2
        binding.rvStory.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        binding.rvStory.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)
            }
        })

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 4000)
    }

    private val runnable = Runnable {
        binding.rvStory.currentItem = binding.rvStory.currentItem + 1
    }


//    private fun showLoading(state: Boolean) {
//        if (state) {
//            dialog = MyCustomDialog()
//            dialog.show(supportFragmentManager, TAG)
//        } else {
//            dialog.dismiss()
//        }
//    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.double_back), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

//    companion object {
//        private const val TAG = "HomeActivity"
//    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        mMap.uiSettings.apply {
//            isZoomControlsEnabled = true
//            isMapToolbarEnabled = true
//            isCompassEnabled = true
//        }
//
//        getMyLocation()
//    }

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            when {
//                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
//                    // Precise location access granted.
//                    getMyLocation()
//                }
//                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
//                    // Only approximate location access granted.
//                    getMyLocation()
//                }
//                else -> {
//                    // No location access granted.
//                }
//            }
//        }
//
//    private fun checkPermission(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            permission
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun getMyLocation() {
//        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
//            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//        ) {
//            mMap.isMyLocationEnabled = true
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    showMarker(location)
//                } else {
//                    Toast.makeText(
//                        this@HomeActivity,
//                        "Location is not found. Try Again",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        } else {
//            requestPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//
//        }
//    }

//    private fun showMarker(location: Location) {
//        homeViewModel.listStory.observe(this) { stories ->
//            stories.forEach {
//                mMap.addMarker(
//                    MarkerOptions()
//                        .position(LatLng(it.lat, it.lon))
//                        .title(it.name)
//                )
//            }
//
//        }
//        val myLocation = LatLng(location.latitude, location.longitude)
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14f))
//    }


}