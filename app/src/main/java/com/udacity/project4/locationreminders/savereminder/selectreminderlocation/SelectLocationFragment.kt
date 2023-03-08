package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.data.model.Resource
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.Locale

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback{

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map : GoogleMap
    private var marker:Marker? = null

    private val fuesdLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        DONE: add the map setup implementation
//        DONE: zoom to the user location after taking his permission
//        DONE: add style to the map
//        DONE: put a marker to location that the user selected
        val mapFrag = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFrag.getMapAsync(this)


//        DONE: call this function after the user confirms on the selected location
        binding.saveBtn.setOnClickListener{
            onLocationSelected()
        }

        return binding.root
    }

    private fun onLocationSelected() {
        //        Done: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        marker?.let {marker ->
            _viewModel.latitude.value = marker.position.latitude
            _viewModel.longitude.value = marker.position.longitude
            _viewModel.reminderSelectedLocationStr.value = marker.title
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }



    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // DONE: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID

            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE

            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN

            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    private fun checkFineLocationPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext() , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCoarseLocationPermission() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext() , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onMapReady(p0: GoogleMap) {
        // DONE("Not yet implemented")
        if (p0 != null) {
            map = p0
            // Setting map style
            try{
                val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                ))
                if(!success){
                    Log.e("Location" , "Error in loading styling")
                }
            }catch (e: Resources.NotFoundException){
                Log.e("Location" , "${e.toString()}")
            }

            // setPOIClick
            map.setOnMapClickListener { poi ->
                map.clear()
                marker = map.addMarker(MarkerOptions().position(LatLng(poi.latitude , poi.longitude)))
                marker?.showInfoWindow()

                map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(poi.latitude,poi.longitude)))

            }

            // setLongClick
            map.setOnMapLongClickListener { latLng ->
                var snippet = String.format(
                    Locale.getDefault(),
                    "LAT: %1$.5f, LONG: %2$.5f",
                    latLng.longitude,
                    latLng.longitude
                )
                map.clear()
                marker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Pin")
                        .snippet(snippet)
                )
                marker?.showInfoWindow()
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }

            // Locations
            if(checkFineLocationPermissions() && checkCoarseLocationPermission()){
                // get user location
                map.isMyLocationEnabled = true
                fuesdLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    val currentLocation = location?.let { LatLng(it.latitude, it.longitude) }
                    currentLocation?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                        ?.let { map.moveCamera(it) }
                    marker= currentLocation?.let {
                        MarkerOptions().position(it)
                            .title("Current Location")
                    }?.let {
                        map.addMarker(
                            it
                        )
                    }
                    marker?.showInfoWindow()
                }
            }else{
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
        }
    }



}
