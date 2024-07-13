package com.bucic.radarisha.ui.radar.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentRadarCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RadarCreateFragment : Fragment() {

    private val viewModel: RadarCreateViewModel by viewModels()
    private var _binding: FragmentRadarCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRadarCreateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radarTypesEnum = RadarType.entries.map { it.displayName }
        val radarTypeArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, radarTypesEnum)
        val typeAutocompleteTextView = binding.typeAutoCompleteTextView
        typeAutocompleteTextView.setAdapter(radarTypeArrayAdapter)

        val speedValues = resources.getIntArray(R.array.radar_speed)
        val speedArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, speedValues.map { it.toString() })
        val speedAutocompleteTextView = binding.speedAutoCompleteTextView
        speedAutocompleteTextView.setAdapter(speedArrayAdapter)

        binding.createRadarButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Selected type: ${typeAutocompleteTextView.text} \n" +
                        "Selected speed: ${speedAutocompleteTextView.text}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // TODO: get current location address
//        binding.tvAddress.text = "Address of current location"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}