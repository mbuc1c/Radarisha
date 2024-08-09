package com.bucic.radarisha.ui.radar.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.DialogRadarInfoBinding
import com.bucic.radarisha.databinding.FragmentMapBinding
import com.bucic.radarisha.entities.RadarMarker
import com.bucic.radarisha.entities.toDomain
import com.bucic.radarisha.mapper.toPresentation
import com.bucic.radarisha.ui.radar.RadarViewModel
import com.bucic.radarisha.util.ReliabilityPresentation
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
import java.util.Date
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
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyTag", "onViewCreated: MapFragment")
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
            val action = MapFragmentDirections.actionMapFragmentToRadarCreateFragment(null)
            findNavController().navigate(action)
        }

        displayVoteStatusMessage()
        observeDialogActionCompletion()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MyTag", "onMapReady: Map is ready")
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
        fetchRadars()
        displayRadars()

        map.setOnMarkerClickListener { marker ->
            try {
                showDialog(marker)
                true
            } catch (e: Exception) {
                false
            }
        }
        getLastKnownLocation()
    }

    private fun observeDialogActionCompletion() {
        startLifecycleScope {
            viewModel.dialogActionCompleted.collect {
                fetchRadars()
            }
        }
    }

    private fun displayVoteStatusMessage() {
        startLifecycleScope {
            viewModel.dialogActionStatusMessage.collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun generateMarkerOptions(radar: RadarMarker): Pair<MarkerOptions, RadarMarker>? {
        val markerOptions = when (radar) {
            is RadarMarker.SpeedCamera -> {
                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon, getReliabilityColor(radar.reliabilityVotes), radar.speed.toString())
                MarkerOptions()
                    .position(LatLng(radar.lat, radar.lng))
                    .title("Speed camera: ${radar.speed} km/h")
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
            is RadarMarker.PoliceCar -> {
                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon, getReliabilityColor(radar.reliabilityVotes))
                MarkerOptions()
                    .position(LatLng(radar.lat, radar.lng))
                    .title("Police car")
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
            is RadarMarker.CarAccident -> {
                val bitmap = VectorDrawableUtils.getBitmapFromVectorDrawable(requireContext(), radar.icon, getReliabilityColor(radar.reliabilityVotes))
                MarkerOptions()
                    .position(LatLng(radar.lat, radar.lng))
                    .title("Car accident")
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
        }
        return markerOptions?.let { Pair(it, radar) }
    }

    private fun addMarkersToMap(markerOptionsList: List<Pair<MarkerOptions, RadarMarker>>) {
        map.clear()
        markerMap.clear()

        for ((markerOptions, radar) in markerOptionsList) {
            val marker = map.addMarker(markerOptions)
            marker?.let { markerMap[it] = radar }
        }
        Log.d("MyTag", "markerMap: $markerMap")
    }

    private fun displayRadars() {
        startLifecycleScope {
            viewModel.radars.collect { result ->
                when (result) {
                    is Result.Success -> {
                        val markerOptionsList = result.data.mapNotNull { radarEntity ->
                            radarEntity.toPresentation()?.let { generateMarkerOptions(it) }
                        }
                        addMarkersToMap(markerOptionsList)
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getReliabilityColor(reliability: ReliabilityPresentation): Int {
        return when (reliability) {
            ReliabilityPresentation.RELIABLE -> R.color.green
            ReliabilityPresentation.UNRELIABLE -> R.color.red
            ReliabilityPresentation.UNKNOWN -> R.color.orange
        }
    }

    private fun startLifecycleScope(action: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch { action() }
    }

    private fun fetchRadars() {
        viewModel.getRadars()
    }

    private fun showDialog(marker: Marker) {
        val radarMarker = markerMap[marker]
        if (activityViewModel.isOwner(radarMarker)) {
            createEditDialog(marker)
        } else createVoteDialog(marker)
    }

    private fun createEditDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogRadarInfoBinding.inflate(layoutInflater)

        displayDialogInfo(dialogBinding, marker)

        builder.setView(dialogBinding.root)
            .setTitle(marker.title)
            .setPositiveButton(getString(R.string.edit)) { dialog, _ ->
                val action = MapFragmentDirections.actionMapFragmentToRadarCreateFragment(markerMap[marker]!!.uid)
                findNavController().navigate(action)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.delete)) { dialog, _ ->
                viewModel.deleteRadar(markerMap[marker]!!.toDomain())
//                marker.remove()
                dialog.dismiss()
            }
            .show()
    }

    private fun createVoteDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogRadarInfoBinding.inflate(layoutInflater)

        displayDialogInfo(dialogBinding, marker)

        builder.setView(dialogBinding.root)
            .setTitle(marker.title)
            .setPositiveButton(getString(R.string.reliable)) { dialog, _ ->
                viewModel.vote(
                    RadarReliabilityVoteEntity(
                        uid = "Placeholder",
                        radarUid = markerMap[marker]!!.uid,
                        voterUid = activityViewModel.userEntity!!.uid,
                        vote = true,
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                )
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.unreliable)) { dialog, _ ->
                viewModel.vote(
                    RadarReliabilityVoteEntity(
                        uid = "Placeholder",
                        radarUid = markerMap[marker]!!.uid,
                        voterUid = activityViewModel.userEntity!!.uid,
                        vote = false,
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                )
                dialog.dismiss()
            }
            .show()
    }

    private fun displayDialogInfo(binding: DialogRadarInfoBinding, marker: Marker) {
        displayAddress(binding, marker)
        displayReliability(binding, marker)
    }

    @SuppressLint("SetTextI18n")
    private fun displayReliability(binding: DialogRadarInfoBinding, marker: Marker) {
        binding.tvReliability.text = "Reliability: ${markerMap[marker]!!.reliabilityVotes.display}"
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