package com.bucic.radarisha.ui.radar.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentMapBinding
import com.bucic.radarisha.entities.RadarMarker
import com.bucic.radarisha.mapper.toPresentation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var isTrackingLocation = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Make code cleaner
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (isTrackingLocation) {
                        updateCamera(location)
                    }
                }
            }
        }

        binding.extendedFab.setOnClickListener {
            findNavController().navigate(R.id.action_MapFragment_to_RadarCreateFragment)
        }
    }

    private suspend fun displayRadars() {
        fetchRadars()
        viewModel.radars.collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    val presentationResult: List<RadarMarker> = result.data.mapNotNull { radarEntity ->
                        radarEntity.toPresentation()
                    }
                    for (radar in presentationResult) {
                        when (radar) {
                            is RadarMarker.SpeedCamera -> {
                                map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Speed camera: ${radar.speed} km/h")
                                )
                            }
                            is RadarMarker.PoliceCar -> {
                                map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Police car")
                                )
                            }
                            is RadarMarker.CarAccident -> {
                                map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Car accident")
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {

                }
                else -> {}
            }
        }
    }

    private fun startLifecycleScope(action: suspend () -> Unit) {
        lifecycleScope.launch { action() }
    }

    private fun fetchRadars() {
        viewModel.getRadars()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isTrafficEnabled = true
        checkLocationPermission()

        map.setOnCameraMoveListener {
            isTrackingLocation = false
        }

        map.setOnMyLocationButtonClickListener {
            isTrackingLocation = true
            getLastKnownLocation()
            true
        }

        startLifecycleScope { displayRadars() }
        getLastKnownLocation()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateCamera(it)
            }
        }
    }

    private fun updateCamera(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }
}