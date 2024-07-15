package com.bucic.radarisha.ui.radar.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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

    private lateinit var initialConstraint: ConstraintSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadarCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialConstraint = ConstraintSet()
        initialConstraint.clone(binding.constraintLayout)

        val radarTypesEnum = RadarType.entries.map { it.displayName }
        val radarTypeArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, radarTypesEnum)
        binding.typeAutoCompleteTextView.setAdapter(radarTypeArrayAdapter)

        val speedValues = resources.getIntArray(R.array.radar_speed)
        val speedArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, speedValues.map { it.toString() })
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
            // temporary
            Toast.makeText(
                requireContext(),
                "Selected type: ${binding.typeAutoCompleteTextView.text} \n" +
                        "Selected speed: ${binding.speedAutoCompleteTextView.text}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // TODO: get current location address
        binding.tvAddress.text = "placeholder"

        removeMenuSpeedAndMoveLocation()
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