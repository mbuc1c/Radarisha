package com.bucic.radarisha.ui.radar.address

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentAddressFinderBinding
import com.bucic.radarisha.util.getAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

// TODO: prekrsti
@AndroidEntryPoint
class AddressFinderFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentAddressFinderBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressFinderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmLocationButton.setOnClickListener {
            selectedLocation?.let { location ->
                lifecycleScope.launch {
                    val address = Geocoder(requireContext(), Locale.getDefault()).getAddress(location.latitude, location.longitude)
                    parentFragmentManager.setFragmentResult(KEY_REQUEST_LOCATION, Bundle().apply {
                        putDouble(KEY_LATITUDE, location.latitude)
                        putDouble(KEY_LONGITUDE, location.longitude)
                        putString(KEY_ADDRESS, address?.getAddressLine(0) ?: "No address found")
                    })
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val initialLocation = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))
                    selectedLocation = initialLocation
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        mMap.setOnCameraIdleListener {
            selectedLocation = mMap.cameraPosition.target
        }
    }

    companion object {
        const val KEY_LATITUDE = "KEY_LATITUDE"
        const val KEY_LONGITUDE = "KEY_LONGITUDE"
        const val KEY_ADDRESS = "KEY_ADDRESS"
        const val KEY_REQUEST_LOCATION = "KEY_REQUEST_LOCATION"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}