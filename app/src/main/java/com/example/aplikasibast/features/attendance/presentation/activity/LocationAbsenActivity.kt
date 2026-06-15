package com.example.aplikasibast.features.attendance.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationAbsenBinding
    private val viewModel: AttendanceViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userMarker: Marker? = null
    private var isMasuk: Boolean = true
    private var isViewOnly: Boolean = false
    
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    private val OFFICE_LAT = -6.162164
    private val OFFICE_LNG = 106.830588
    private val MAX_RADIUS = 100.0

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
        
        if (lat != 0.0 && lng != 0.0) updateMapPosition(GeoPoint(lat, lng))
    }

    private fun setupUI() {
        binding.btnAbsenMasuk.text = if (isMasuk) "ABSEN MASUK" else "ABSEN KELUAR"
        binding.btnAbsenMasuk.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (isMasuk) "#290F65" else "#D32F2F")
        )
        
        binding.btnAbsenMasuk.setOnClickListener {
            if (currentLat == 0.0) return@setOnClickListener
            
            val results = FloatArray(1)
            Location.distanceBetween(currentLat, currentLng, OFFICE_LAT, OFFICE_LNG, results)
            
            if (results[0] <= MAX_RADIUS) {
                val intent = Intent(this, CameraAbsenActivity::class.java).apply {
                    putExtra("IS_MASUK", isMasuk)
                    putExtra("LOKASI", binding.tvAlamatLengkap.text.toString())
                    putExtra("LAT", currentLat)
                    putExtra("LNG", currentLng)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Di luar radius: ${results[0].toInt()}m", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(18.5)

        val officePoint = GeoPoint(OFFICE_LAT, OFFICE_LNG)
        val officeMarker = Marker(binding.mapView)
        officeMarker.position = officePoint
        officeMarker.title = "Kantor BAST"
        binding.mapView.overlays.add(officeMarker)

        val circle = Polygon(binding.mapView)
        circle.points = Polygon.pointsAsCircle(officePoint, MAX_RADIUS)
        circle.fillPaint.color = Color.parseColor("#325D2D91")
        binding.mapView.overlays.add(circle)
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLat = it.latitude
                currentLng = it.longitude
                updateMapPosition(GeoPoint(it.latitude, it.longitude))
                updateDistanceUI(it)
            }
        }
    }

    private fun updateDistanceUI(loc: Location) {
        val results = FloatArray(1)
        Location.distanceBetween(loc.latitude, loc.longitude, OFFICE_LAT, OFFICE_LNG, results)
        val distance = results[0]
        binding.tvDistanceInfo.text = "Jarak ke kantor: ${distance.toInt()} meter"
        binding.tvDistanceInfo.setTextColor(if (distance <= MAX_RADIUS) Color.GREEN else Color.RED)
    }

    private fun updateMapPosition(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        if (userMarker == null) { userMarker = Marker(binding.mapView); binding.mapView.overlays.add(userMarker) }
        userMarker?.position = point
        binding.mapView.invalidate()
    }
}
