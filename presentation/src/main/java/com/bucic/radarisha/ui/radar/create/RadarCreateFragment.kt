package com.bucic.radarisha.ui.radar.create

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import com.bucic.domain.util.Result
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
import kotlinx.coroutines.flow.collectLatest
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
    private val args: RadarCreateFragmentArgs by navArgs()

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

        if (args.radarUid != null && !viewModel.radarDataFetched) {
            viewModel.getRadar(args.radarUid!!)
            viewModel.radarDataFetched = true
        }

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
            viewModel.radarLocation = LatLng(latitude, longitude)
            viewModel.currentAddress = address
            binding.tvAddress.text = address
        }

        displayCreateRadarStatusMessage()
    }

    private fun displayCreateRadarStatusMessage() {
        startLifecycleScope {
            viewModel.createRadarStatusMessage.collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }

                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
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
                    viewModel.radarLocation = LatLng(it.latitude, it.longitude)
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
        viewLifecycleOwner.lifecycleScope.launch { action() }
    }

    private fun setUpUI() {

        binding.typeAutoCompleteTextView.setAdapter(viewModel.radarTypeAdapter)
        binding.speedAutoCompleteTextView.setAdapter(viewModel.speedAdapter)

        if (args.radarUid == null) {
            binding.createRadarButton.text = getString(R.string.create)
            binding.createRadarButton.setOnClickListener {
                createRadar()
            }
        } else {
            binding.createRadarButton.text = getString(R.string.update)
            binding.createRadarButton.setOnClickListener {
                updateRadar()
            }
        }

        startLifecycleScope {
            viewModel.radarForUpdate.collect { result ->
                when (result) {
                    is Result.Success -> {
                        viewModel.selectedRadarType = result.data.type.display
                        viewModel.selectedSpeed = result.data.speed?.toString()
                        viewModel.radarLocation = LatLng(result.data.lat, result.data.lng)
                        viewModel.currentAddress = getAddress(result.data.lat, result.data.lng)
                        updateUIWithViewModelData()
                    }

                    else -> {}
                }
            }
        }

        binding.typeAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedType = parent.getItemAtPosition(position) as String
            viewModel.selectedRadarType = selectedType
            removeErrorForField(binding.menuRadarType)
            if (selectedType != RadarType.SPEED_CAMERA.display) {
                disableMenuSpeed()
            } else {
                enableMenuSpeed()
            }
        }

        binding.speedAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            removeErrorForField(binding.menuSpeed)
        }

        binding.findOnMapButton.setOnClickListener {
            navigateToAddressFinder()
        }

        updateUIWithViewModelData()
    }

    private suspend fun getAddress(lat: Double, lng: Double): String? {
        return Geocoder(requireContext(), Locale.getDefault()).getAddress(lat, lng)
            ?.getAddressLine(0)
    }

    private fun updateUIWithViewModelData() {
        viewModel.selectedRadarType?.let {
            binding.typeAutoCompleteTextView.setText(it, false)
        }
        viewModel.selectedSpeed?.let {
            binding.speedAutoCompleteTextView.setText(it, false)
        }
        viewModel.currentAddress?.let { binding.tvAddress.text = it }
        if (viewModel.selectedRadarType == RadarType.SPEED_CAMERA.display) {
            enableMenuSpeed()
        } else {
            disableMenuSpeed()
        }
    }

    private fun createRadar() {
        Log.d("MyTag", "createRadar started")
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
                        lat = viewModel.radarLocation?.latitude ?: 0.0,
                        lng = viewModel.radarLocation?.longitude ?: 0.0,
                        type = RadarType.entries.find { it.display == binding.typeAutoCompleteTextView.text.toString() }!!,
                        speed = getSpeedValue(),
                        createdAt = Date(),
                        updatedAt = null,
                        reliabilityVotes = emptyList()
                    )
                )
            }
        }
    }

    private fun updateRadar() {
        Log.d("MyTag", "updateRadar started")
        showErrorForField(binding.menuRadarType, getString(R.string.error_empty_radar_type_field))
        if (binding.menuSpeed.isVisible) {
            showErrorForField(binding.menuSpeed, getString(R.string.error_empty_radar_speed_field))
        }

        if (!createRadarError) {
            startLifecycleScope {
                viewModel.updateRadar(
                    RadarEntity(
                        uid = args.radarUid!!,
                        creatorUid = activityViewModel.userEntity?.uid
                            ?: "Error with fetching user uid",
                        lat = viewModel.radarLocation?.latitude ?: 0.0,
                        lng = viewModel.radarLocation?.longitude ?: 0.0,
                        type = RadarType.entries.find { it.display == binding.typeAutoCompleteTextView.text.toString() }!!,
                        speed = getSpeedValue(),
                        createdAt = Date(),
                        updatedAt = Date(),
                        reliabilityVotes = emptyList()
                    )
                )
            }
        }
    }

    private fun showErrorForField(field: TextInputLayout, message: String) {
        val text = field.editText?.text?.toString()
        if (text.isNullOrEmpty()) {
            field.error = message
            createRadarError = true
        } else {
            field.error = null  // Clear the error if the field is not empty
        }
    }

    private fun removeErrorForField(field: TextInputLayout) {
        field.error = null
        createRadarError = false
    }

    // TODO: catch null
    private fun getSpeedValue(): Int? {
        return if (binding.typeAutoCompleteTextView.text.toString() != RadarType.SPEED_CAMERA.display) {
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