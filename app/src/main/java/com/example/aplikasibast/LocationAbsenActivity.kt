package com.example.aplikasibast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
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
import org.osmdroid.views.overlay.Polygon
import java.util.Locale

class LocationAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationAbsenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userMarker: Marker? = null
    private var isMasuk: Boolean = true
    private var isViewOnly: Boolean = false
    
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    // Koordinat Kantor Baru
    private val OFFICE_LAT = -6.1597
    private val OFFICE_LNG = 106.8319
    private val MAX_RADIUS = 100.0 // Radius maksimal dalam meter

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
        setupListeners()

        if (isViewOnly) {
            setupViewOnlyMode()
        } else {
            setupUI()
            checkLocationPermissions()
        }
    }

    private fun setupViewOnlyMode() {
        binding.btnAbsenMasuk.visibility = View.GONE
        binding.tvDistanceInfo.visibility = View.GONE
        binding.tvLabelTitle.text = intent.getStringExtra("TITLE_TO_VIEW") ?: "Detail Lokasi"
        
        val time = intent.getStringExtra("TIME_TO_VIEW")
        if (!time.isNullOrEmpty() && time != "-") {
            binding.tvWaktuDetail.text = "Waktu: $time"
            binding.tvWaktuDetail.visibility = View.VISIBLE
        }

        val lat = intent.getDoubleExtra("LAT_TO_VIEW", 0.0)
        val lng = intent.getDoubleExtra("LNG_TO_VIEW", 0.0)
        val address = intent.getStringExtra("ADDRESS_TO_VIEW")

        binding.tvAlamatLengkap.text = address ?: "Alamat tidak tersedia"
        
        if (lat != 0.0 && lng != 0.0) {
            updateMapPosition(GeoPoint(lat, lng))
        }
    }

    private fun setupUI() {
        binding.btnAbsenMasuk.visibility = View.VISIBLE
        binding.tvWaktuDetail.visibility = View.GONE
        binding.tvLabelTitle.text = "Lokasi Terkini Anda"
        
        if (!isMasuk) {
            binding.btnAbsenMasuk.text = "ABSEN KELUAR"
            binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
        } else {
            binding.btnAbsenMasuk.text = "ABSEN MASUK"
            binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#290F65"))
        }
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(18.5)

        // Tambahkan Marker Kantor sebagai referensi
        val officePoint = GeoPoint(OFFICE_LAT, OFFICE_LNG)
        val officeMarker = Marker(binding.mapView)
        officeMarker.position = officePoint
        officeMarker.title = "Kantor BAST"
        officeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.overlays.add(officeMarker)

        // Tambahkan Lingkaran Radius (Visualisasi)
        // PERBAIKAN: Gunakan Polygon.pointsAsCircle untuk membuat titik-titik lingkaran
        val circle = Polygon(binding.mapView)
        circle.points = Polygon.pointsAsCircle(officePoint, MAX_RADIUS)
        circle.fillPaint.color = Color.parseColor("#325D2D91") // Ungu transparan
        circle.outlinePaint.color = Color.parseColor("#5D2D91")
        circle.outlinePaint.strokeWidth = 2f
        binding.mapView.overlays.add(circle)
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLng = location.longitude
                updateMapPosition(GeoPoint(currentLat, currentLng))
                getAddressFromLocation(currentLat, currentLng)
                
                // Hitung Jarak dan Update UI
                val results = FloatArray(1)
                Location.distanceBetween(currentLat, currentLng, OFFICE_LAT, OFFICE_LNG, results)
                updateDistanceUI(results[0])
            }
        }
    }

    private fun updateDistanceUI(distance: Float) {
        val distanceText = String.format("Jarak ke kantor: %.0f meter", distance)
        
        if (distance <= MAX_RADIUS) {
            binding.tvDistanceInfo.text = "$distanceText (Dalam Radius)"
            binding.tvDistanceInfo.setTextColor(Color.parseColor("#2E7D32")) // Hijau
        } else {
            binding.tvDistanceInfo.text = "$distanceText (Luar Radius)"
            binding.tvDistanceInfo.setTextColor(Color.parseColor("#D32F2F")) // Merah
        }
    }

    private fun updateMapPosition(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        if (userMarker == null) {
            userMarker = Marker(binding.mapView)
            binding.mapView.overlays.add(userMarker)
        }
        userMarker?.position = point
        userMarker?.title = "Lokasi Anda"
        userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.invalidate()
    }

    private fun getAddressFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                binding.tvAlamatLengkap.text = addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            binding.tvAlamatLengkap.text = "Gagal mengambil alamat"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAbsenMasuk.setOnClickListener {
            if (currentLat == 0.0 && currentLng == 0.0) {
                Toast.makeText(this, "Lokasi belum terdeteksi. Mohon tunggu sebentar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val results = FloatArray(1)
            Location.distanceBetween(currentLat, currentLng, OFFICE_LAT, OFFICE_LNG, results)
            val distanceInMeters = results[0]

            if (distanceInMeters <= MAX_RADIUS) {
                val intent = Intent(this, CameraAbsenActivity::class.java)
                intent.putExtra("IS_MASUK", isMasuk)
                intent.putExtra("LOKASI", binding.tvAlamatLengkap.text.toString())
                intent.putExtra("LAT", currentLat)
                intent.putExtra("LNG", currentLng)
                startActivity(intent)
            } else {
                val message = String.format(
                    "Gagal: Anda berada di luar radius (%.0f m). Maksimal 100m.",
                    distanceInMeters
                )
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
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
