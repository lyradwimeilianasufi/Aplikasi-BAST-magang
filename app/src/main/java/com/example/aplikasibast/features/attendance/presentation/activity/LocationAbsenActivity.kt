package com.example.aplikasibast.features.attendance.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityLocationAbsenBinding
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class LocationAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationAbsenBinding
    private val viewModel: AttendanceViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userMarker: Marker? = null
    private var isMasuk: Boolean = true
    private var isViewOnly: Boolean = false
    
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var currentDistance: Float = -1f

    // Titik Kantor PT Sumber Berkah Logistik (SBL)
    // Jl. Krekot Bunder Raya No.22, Pasar Baru, Sawah Besar, Jakarta Pusat
    private val OFFICE_LAT = -6.1599721
    private val OFFICE_LNG = 106.832255

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) getCurrentLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isViewOnly = intent.getBooleanExtra("IS_VIEW_ONLY", false)
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

        setupMap()
        if (isViewOnly) setupViewOnlyMode() else { setupUI(); checkLocationPermissions() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupViewOnlyMode() {
        binding.btnAbsenMasuk.visibility = View.GONE
        binding.tvDistanceInfo.visibility = View.GONE
        binding.tvLabelTitle.text = intent.getStringExtra("TITLE_TO_VIEW") ?: "Detail Lokasi"
        
        val lat = intent.getDoubleExtra("LAT_TO_VIEW", 0.0)
        val lng = intent.getDoubleExtra("LNG_TO_VIEW", 0.0)
        binding.tvAlamatLengkap.text = intent.getStringExtra("ADDRESS_TO_VIEW") ?: "Alamat tidak tersedia"
        
        if (lat != 0.0 && lng != 0.0) {
            currentLat = lat
            currentLng = lng
            updateMapPosition(GeoPoint(lat, lng))
        }
    }

    private fun setupUI() {
        binding.btnAbsenMasuk.text = if (isMasuk) "ABSEN MASUK" else "ABSEN KELUAR"
        binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (isMasuk) "#290F65" else "#D32F2F")
        )
        
        binding.tvDistanceInfo.visibility = View.GONE
        
        binding.btnAbsenMasuk.setOnClickListener {
            if (currentLat == 0.0) {
                Toast.makeText(this, "Mendapatkan lokasi...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentDistance > 100) {
                Toast.makeText(this, "Gagal: Anda berada di luar radius kantor (Maks 100m)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            
            val intent = Intent(this, CameraAbsenActivity::class.java).apply {
                putExtra("IS_MASUK", isMasuk)
                putExtra("LOKASI", binding.tvAlamatLengkap.text.toString())
                putExtra("LAT", currentLat)
                putExtra("LNG", currentLng)
            }
            startActivity(intent)
        }
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(18.5)

        val officePoint = GeoPoint(OFFICE_LAT, OFFICE_LNG)
        val officeMarker = Marker(binding.mapView)
        officeMarker.position = officePoint
        officeMarker.title = "Titik Kantor"
        binding.mapView.overlays.add(officeMarker)
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    handleNewLocation(it.latitude, it.longitude)
                } ?: run {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        lastLoc?.let {
                            handleNewLocation(it.latitude, it.longitude)
                        } ?: run {
                            Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }

    private fun handleNewLocation(lat: Double, lng: Double) {
        currentLat = lat
        currentLng = lng
        updateMapPosition(GeoPoint(lat, lng))
        getAddressFromLocation(lat, lng)
        checkDistance(lat, lng)
    }

    private fun checkDistance(lat: Double, lng: Double) {
        val results = FloatArray(1)
        Location.distanceBetween(OFFICE_LAT, OFFICE_LNG, lat, lng, results)
        currentDistance = results[0]

        binding.tvDistanceInfo.visibility = View.VISIBLE
        // DEBUG: Tampilkan koordinat di UI agar bisa dibaca AI
        binding.tvDistanceInfo.text = "COORD: $lat, $lng | Jarak: ${currentDistance.toInt()}m"
        
        if (currentDistance <= 100) {
            binding.tvDistanceInfo.setTextColor(Color.parseColor("#27AE60"))
        } else {
            binding.tvDistanceInfo.setTextColor(Color.parseColor("#D32F2F"))
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.tvAlamatLengkap.text = address
            } else {
                binding.tvAlamatLengkap.text = "Alamat tidak ditemukan ($lat, $lng)"
            }
        } catch (e: Exception) {
            binding.tvAlamatLengkap.text = "Gagal memuat alamat ($lat, $lng)"
            e.printStackTrace()
        }
    }

    private fun updateMapPosition(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        if (userMarker == null) { 
            userMarker = Marker(binding.mapView)
            binding.mapView.overlays.add(userMarker) 
        }
        userMarker?.position = point
        binding.mapView.invalidate()
    }
}
