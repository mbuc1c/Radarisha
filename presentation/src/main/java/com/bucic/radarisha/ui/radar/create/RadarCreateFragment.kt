package com.bucic.radarisha.ui.radar.create

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.RadarType
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentRadarCreateBinding
import com.bucic.radarisha.ui.radar.RadarViewModel
import com.bucic.radarisha.ui.radar.address.AddressFinderFragment
import com.bucic.radarisha.ui.radar.address.AddressFinderFragment.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.bucic.radarisha.util.getAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RadarCreateFragment : Fragment() {

    private val activityViewModel: RadarViewModel by activityViewModels()
    private val viewModel: RadarCreateViewModel by viewModels()
    private var _binding: FragmentRadarCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var initialConstraint: ConstraintSet
    private lateinit var newRadarLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var locationFetched = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadarCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            if (!locationFetched) {
                // Fetch location and address only if it hasn't been fetched yet
                fetchCurrentLocationAndAddress()
                locationFetched = true // Set flag to true after fetching location
            }
        }

        setUpUI()

        parentFragmentManager.setFragmentResultListener(
            AddressFinderFragment.KEY_REQUEST_LOCATION,
            viewLifecycleOwner
        ) { _, bundle ->
            val latitude = bundle.getDouble(AddressFinderFragment.KEY_LATITUDE)
            val longitude = bundle.getDouble(AddressFinderFragment.KEY_LONGITUDE)
            val address = bundle.getString(AddressFinderFragment.KEY_ADDRESS)
            newRadarLocation = LatLng(latitude, longitude)
            binding.tvAddress.text = address
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocationAndAddress() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    newRadarLocation = LatLng(it.latitude, it.longitude)
                    updateCurrentLocationUI(it.latitude, it.longitude)
                }
            }
    }

    private fun updateCurrentLocationUI(latitude: Double, longitude: Double) {
        startLifecycleScope {
            val address = geocoder.getAddress(latitude, longitude)
            address?.let {
                withContext(Dispatchers.Main) {
                    binding.tvAddress.text = it.getAddressLine(0)
                }
            }
        }
    }

    private fun startLifecycleScope(action: suspend () -> Unit) {
        lifecycleScope.launch { action() }
    }

    private fun setUpUI() {
        initialConstraint = ConstraintSet()
        initialConstraint.clone(binding.constraintLayout)

        val radarTypesEnum = RadarType.entries.map { it.displayName }
        val radarTypeArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, radarTypesEnum)
        binding.typeAutoCompleteTextView.setAdapter(radarTypeArrayAdapter)

        val speedValues = resources.getIntArray(R.array.radar_speed)
        val speedArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            speedValues.map { it.toString() })
        binding.speedAutoCompleteTextView.setAdapter(speedArrayAdapter)

        binding.typeAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedType = parent.getItemAtPosition(position) as String
            if (selectedType != RadarType.SPEED_CAMERA.displayName) {
                removeMenuSpeedAndMoveLocation()
            } else {
                revertToOriginalConstraints()
            }
        }

        binding.createRadarButton.setOnClickListener {
            createRadar()
        }

        removeMenuSpeedAndMoveLocation()

        binding.findOnMapButton.setOnClickListener {
            navigateToAddressFinder()
        }
    }

    private fun createRadar() {
        startLifecycleScope {
            viewModel.createRadar(
                RadarEntity(
                    uid = "Placeholder",
                    creatorUid = activityViewModel.userEntity?.uid ?: "Error with fetching user uid",
                    lat = newRadarLocation.latitude,
                    lng = newRadarLocation.longitude,
                    type = RadarType.entries.find { it.displayName == binding.typeAutoCompleteTextView.text.toString() }!!,
                    speed = getSpeedValue(),
                    createdAt = Date(),
                    updatedAt = null
                )
            )
        }
        findNavController().navigateUp()
    }

    private fun getSpeedValue(): Int? {
        return if (binding.typeAutoCompleteTextView.text.toString() != RadarType.SPEED_CAMERA.displayName) {
            null
        } else binding.speedAutoCompleteTextView.text.toString().toInt()
    }
    private fun navigateToAddressFinder() {
        findNavController().navigate(R.id.action_RadarCreateFragment_to_AddressFinderFragment)
    }

    private fun revertToOriginalConstraints() {
        binding.constraintLayout.addView(binding.menuSpeed)
        initialConstraint.applyTo(binding.constraintLayout)
    }

    private fun removeMenuSpeedAndMoveLocation() {
        with(binding) {
            constraintLayout.removeView(menuSpeed)

            val constraintSet = ConstraintSet()
            with(constraintSet) {
                clone(constraintLayout)
                connect(tvLocation.id, ConstraintSet.TOP, menuRadarType.id, ConstraintSet.BOTTOM)
                applyTo(constraintLayout)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}