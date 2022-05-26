package com.example.intermediatesubmission_1.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.databinding.ActivityMapsBinding
import com.example.intermediatesubmission_1.databinding.ItemDetailMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso

class MapsActivity :AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var positionStory: LatLng
    private lateinit var token: String
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra("token").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel.getStory(token)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mapsViewModel.listStory.observe(this) { story ->
            mMap.clear()
            story.forEach {
                lat = it.lat
                lon = it.lon
                positionStory = LatLng(lat, lon)
                mMap.addMarker(MarkerOptions()
                    .position(positionStory)
                    .title(it.name)
                    .snippet("::1lat: ${it.lat}::2lon: ${it.lon}::3${it.photoUrl}::4${it.description}::5")

                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionStory, 15f))
                getDetailMaps()
            }
        }

        getMyLocation()
        setMapStyle()
    }

    private fun getDetailMaps() {
        mMap.setInfoWindowAdapter(object: GoogleMap.InfoWindowAdapter {
            private val binding: ItemDetailMapBinding = ItemDetailMapBinding.inflate(layoutInflater)

            override fun getInfoContents(p0: Marker): View? {
                return null
            }

            @SuppressLint("CheckResult")
            override fun getInfoWindow(p0: Marker): View {
                binding.nameStory.text = p0.title
                binding.descStory.text = p0.snippet!!.substringAfter("::4").substringBefore("::5", "No Description")
                binding.latStory.text = p0.snippet!!.substringAfter("::1").substringBefore("::2", "No Latitude")
                binding.lonStory.text = p0.snippet!!.substringAfter("::2").substringBefore("::3", "No Longitude")

                Picasso.get()
                    .load(p0.snippet!!.substringAfter("::3").substringBefore("::4", "https://upload.wikimedia.org/wikipedia/commons/7/77/Google_Images_2015_logo.svg"))
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.imgItemStoryMaps)
                return binding.root
            }
        })
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        const val TAG = "maps"
    }
}