package com.bucic.radarisha.ui.radar.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.DialogRadarInfoBinding
import com.bucic.radarisha.databinding.FragmentMapBinding
import com.bucic.radarisha.entities.RadarMarker
import com.bucic.radarisha.mapper.toPresentation
import com.bucic.radarisha.ui.radar.RadarViewModel
import com.bucic.radarisha.util.VectorDrawableUtils
import com.bucic.radarisha.util.getAddress
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private val activityViewModel: RadarViewModel by activityViewModels()
    private val viewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var locationCallback: LocationCallback
    private val markerMap = mutableMapOf<Marker, RadarMarker>()

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
        geocoder = Geocoder(requireContext(), Locale.getDefault())

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
        // TODO: Make code cleaner
        viewModel.radars.collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    val presentationResult: List<RadarMarker> = result.data.mapNotNull { radarEntity ->
                        radarEntity.toPresentation()
                    }
                    for (radar in presentationResult) {
                        when (radar) {
                            is RadarMarker.SpeedCamera -> {
                                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon, radar.speed.toString())
                                val marker = map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Speed camera: ${radar.speed} km/h")
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                )
                                marker?.let { markerMap.put(it, radar) }
                            }
                            is RadarMarker.PoliceCar -> {
                                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon)
                                val marker = map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Police car")
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                )
                                marker?.let { markerMap.put(it, radar) }

                            }
                            is RadarMarker.CarAccident -> {
                                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon)
                                val marker = map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(radar.lat, radar.lng))
                                        .title("Car accident")
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                )
                                marker?.let { markerMap.put(it, radar) }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
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
        map.setOnMarkerClickListener { marker ->
            showVoteDialog(marker)
            true
        }
        getLastKnownLocation()
    }

    @SuppressLint("SetTextI18n")
    private fun showVoteDialog(marker: Marker) {
//        val dialogFragment = RadarInfoDialogFragment()
//        dialogFragment.marker = marker
//        dialogFragment.show(parentFragmentManager, "RadarInfoDialog")

        val radarMarker = markerMap[marker]
        if (activityViewModel.isOwner(radarMarker)) {
            createEditDialog(marker)
        } else createVoteDialog(marker)
    }
    // TODO: Modify clicks
    private fun createEditDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogRadarInfoBinding.inflate(layoutInflater)

        displayAddress(dialogBinding, marker)

        builder.setView(dialogBinding.root)
            .setTitle(marker.title)
            .setPositiveButton(getString(R.string.edit)) { dialog, which ->
                Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.delete)) { dialog, which ->
                Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun createVoteDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogRadarInfoBinding.inflate(layoutInflater)

        displayAddress(dialogBinding, marker)

        builder.setView(dialogBinding.root)
            .setTitle(marker.title)
            .setPositiveButton(getString(R.string.reliable)) { dialog, which ->
                Toast.makeText(requireContext(), "Reliable", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.unreliable)) { dialog, which ->
                Toast.makeText(requireContext(), "Unreliable", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun displayAddress(binding: DialogRadarInfoBinding, marker: Marker) = startLifecycleScope {
        val address = geocoder.getAddress(marker.position.latitude, marker.position.longitude)
        address?.let {
            withContext(Dispatchers.Main) {
                binding.tvAddress.text = it.getAddressLine(0)
            }
        }
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