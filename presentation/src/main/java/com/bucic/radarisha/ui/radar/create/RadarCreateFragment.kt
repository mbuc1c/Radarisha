package com.bucic.radarisha.ui.radar.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}