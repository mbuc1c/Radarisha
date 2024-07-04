package com.bucic.radarisha.ui.radar.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.extendedFab.setOnClickListener {
            findNavController().navigate(R.id.action_MapFragment_to_RadarCreateFragment)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val unizd = LatLng(44.113141, 15.237000)
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.addMarker(
            MarkerOptions()
                .position(unizd)
                .title("University of Zadar")
        )
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(unizd))
//        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        googleMap.isTrafficEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}