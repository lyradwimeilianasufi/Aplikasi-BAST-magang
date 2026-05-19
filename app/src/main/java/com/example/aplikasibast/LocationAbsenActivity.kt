package com.example.aplikasibast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
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
    private var isMasuk: Boolean = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
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

        isMasuk = intent.getBooleanExtra("IS_MASUK", true)

        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        binding = ActivityLocationAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupMap()
        setupListeners()
        checkLocationPermissions()
    }

    private fun setupUI() {
        if (!isMasuk) {
            // Jika Absen Keluar: Ubah teks dan warna tombol menjadi merah
            binding.btnAbsenMasuk.text = "ABSEN KELUAR"
            binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
        } else {
            // Jika Absen Masuk: Gunakan teks default dan warna ungu gelap
            binding.btnAbsenMasuk.text = "ABSEN MASUK"
            binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#290F65"))
        }
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
            intent.putExtra("IS_MASUK", isMasuk)
            intent.putExtra("LOKASI", binding.tvAlamatLengkap.text.toString())
            startActivity(intent)
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
