package com.example.aplikasibast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityLocationAbsenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class LocationAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationAbsenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userMarker: Marker? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi OSMDroid sebelum setContentView
        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        binding = ActivityLocationAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupMap()
        setupListeners()
        checkLocationPermissions()
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        val mapController = binding.mapView.controller
        mapController.setZoom(18.0)
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userPoint = GeoPoint(location.latitude, location.longitude)
                updateMapPosition(userPoint)
                getAddressFromLocation(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMapPosition(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        
        if (userMarker == null) {
            userMarker = Marker(binding.mapView)
            binding.mapView.overlays.add(userMarker)
        }
        userMarker?.position = point
        userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.invalidate()
    }

    private fun getAddressFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.tvAlamatLengkap.text = address
            } else {
                binding.tvAlamatLengkap.text = "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            binding.tvAlamatLengkap.text = "Gagal mengambil alamat: ${e.message}"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAbsenMasuk.setOnClickListener {
            val intent = Intent(this, CameraAbsenActivity::class.java)
            startActivity(intent)
        }

        binding.btnMyLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
