package com.bucic.radarisha.ui.radar.create

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentRadarCreateBinding
import com.bucic.radarisha.ui.radar.RadarViewModel
import com.bucic.radarisha.ui.radar.address.AddressFinderFragment
import com.bucic.radarisha.util.getAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var createRadarError = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

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

        checkPermission {
            if (!viewModel.currentLocationFetched) {
                fetchCurrentLocationAndAddress()
                viewModel.currentLocationFetched = true
            } else {
                updateUIWithViewModelData()
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
            viewModel.newRadarLocation = LatLng(latitude, longitude)
            viewModel.currentAddress = address
            binding.tvAddress.text = address
        }
    }

    private fun checkPermission(action: () -> Unit) {
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
        } else action()
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocationAndAddress() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    viewModel.newRadarLocation = LatLng(it.latitude, it.longitude)
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
                    viewModel.currentAddress = it.getAddressLine(0)
                }
            }
        }
    }

    private fun startLifecycleScope(action: suspend () -> Unit) {
        lifecycleScope.launch { action() }
    }

    private fun setUpUI() {

        binding.typeAutoCompleteTextView.setAdapter(viewModel.radarTypeAdapter)
        binding.speedAutoCompleteTextView.setAdapter(viewModel.speedAdapter)


        binding.typeAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedType = parent.getItemAtPosition(position) as String
            viewModel.selectedRadarType = selectedType
            removeErrorForField(binding.menuRadarType)
            if (selectedType != RadarType.SPEED_CAMERA.displayName) {
                disableMenuSpeed()
            } else {
                enableMenuSpeed()
            }
        }

        binding.speedAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            removeErrorForField(binding.menuSpeed)
        }

        binding.createRadarButton.setOnClickListener {
            createRadar()
        }

        binding.findOnMapButton.setOnClickListener {
            navigateToAddressFinder()
        }

        updateUIWithViewModelData()
    }

    private fun updateUIWithViewModelData() {
        viewModel.selectedRadarType?.let {
            binding.typeAutoCompleteTextView.setText(it, false)
        }
        viewModel.selectedSpeed?.let {
            binding.speedAutoCompleteTextView.setText(it, false)
        }
        viewModel.currentAddress?.let { binding.tvAddress.text = it }
        if (viewModel.selectedRadarType == RadarType.SPEED_CAMERA.displayName) {
            enableMenuSpeed()
        } else {
            disableMenuSpeed()
        }
    }

    private fun createRadar() {
        showErrorForField(binding.menuRadarType, getString(R.string.error_empty_radar_type_field))
        if (binding.menuSpeed.isVisible) {
            showErrorForField(binding.menuSpeed, getString(R.string.error_empty_radar_speed_field))
        }

        if (!createRadarError) {
            startLifecycleScope {
                viewModel.createRadar(
                    RadarEntity(
                        uid = "Placeholder",
                        creatorUid = activityViewModel.userEntity?.uid
                            ?: "Error with fetching user uid",
                        lat = viewModel.newRadarLocation?.latitude ?: 0.0,
                        lng = viewModel.newRadarLocation?.longitude ?: 0.0,
                        type = RadarType.entries.find { it.displayName == binding.typeAutoCompleteTextView.text.toString() }!!,
                        speed = getSpeedValue(),
                        createdAt = Date(),
                        updatedAt = null
                    )
                )
            }
            findNavController().navigateUp()
        }
    }

    private fun showErrorForField(field: TextInputLayout, message: String) {
        if (field.editText?.text?.isEmpty() == true) {
            field.error = message
            createRadarError = true
        }
    }

    private fun removeErrorForField(field: TextInputLayout) {
        field.error = null
        createRadarError = false
    }

    private fun getSpeedValue(): Int? {
        return if (binding.typeAutoCompleteTextView.text.toString() != RadarType.SPEED_CAMERA.displayName) {
            null
        } else binding.speedAutoCompleteTextView.text.toString().toInt()
    }

    private fun navigateToAddressFinder() {
        findNavController().navigate(R.id.action_RadarCreateFragment_to_AddressFinderFragment)
    }

    private fun enableMenuSpeed() {
        binding.menuSpeed.isEnabled = true
        binding.menuSpeed.visibility = View.VISIBLE
    }

    private fun disableMenuSpeed() {
        binding.menuSpeed.isEnabled = false
        binding.menuSpeed.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}