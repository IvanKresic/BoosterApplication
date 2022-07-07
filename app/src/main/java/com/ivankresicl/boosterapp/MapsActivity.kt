package com.ivankresicl.boosterapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ivankresicl.boosterapp.api.RetrofitClient
import com.ivankresicl.boosterapp.models.OrderModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val bottomSheetView by lazy { findViewById<ConstraintLayout>(R.id.bottomSheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var userLocation : LatLng
    lateinit var marker : Marker
    lateinit var markerIcon : BitmapDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        markerIcon = bitmapDescriptorFromVector(this, R.drawable.ic_gas_point)!!

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        setBottomSheetVisibility(false)
        next_button.setOnClickListener {
            setBottomSheetVisibility(true)
        }
        order_button.setOnClickListener {

            val requestBoostOrder = RetrofitClient.instance?.getMyApi()?.requestBoostOrder(
                OrderModel(
                    delivery_time_dropdown.text.toString(),
                    payment_dropdown.text.toString(),
                    marker.position.longitude,
                    marker.position.latitude))
            requestBoostOrder?.enqueue(
                object : Callback<OrderModel?> {
                    override fun onResponse(
                        call: Call<OrderModel?>?,
                        response: Response<OrderModel?>
                    ) {
                        val myOrder: OrderModel? = response.body()
                        val intent = Intent(applicationContext, OrderActivity::class.java).apply {
                            putExtra("DELIVERY_TIME", myOrder?.deliveryTime)
                            putExtra("PAYMENT_TYPE", myOrder?.paymentType)
                            putExtra("DELIVERY_LONGITUDE", myOrder?.longitude)
                            putExtra("DELIVERY_LATITUDE", myOrder?.latitude)
                        }
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<OrderModel?>?, t: Throwable?) {
                        Toast.makeText(
                            applicationContext,
                            "An error has occured: " + t?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        val deliveryTimeAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.list_item,
            resources.getStringArray(R.array.delivery_time_windows))
        delivery_time_dropdown.setAdapter(deliveryTimeAdapter)

        val paymentAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.list_item,
            resources.getStringArray(R.array.payment_method))
        payment_dropdown.setAdapter(paymentAdapter)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        my_position.setOnClickListener{
            setBottomSheetVisibility(false)
            if(this::userLocation.isInitialized){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    userLocation, 20.0f
                ))
                if(this@MapsActivity::marker.isInitialized)
                    marker.position = googleMap.cameraPosition.target
            }
        }

        map_style.setOnClickListener {
            if(googleMap.mapType == GoogleMap.MAP_TYPE_SATELLITE)
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            else
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        googleMap.apply {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        userLocation = LatLng(
                            location.latitude,
                            location.longitude
                        )
                        marker = googleMap.addMarker(MarkerOptions().position(userLocation).title("Your vehicle location!").icon(markerIcon))
                        /*setOnMarkerClickListener { marker ->
                            onMarkerClicked(marker)
                            false
                        }*/
                        moveCamera(CameraUpdateFactory.newLatLngZoom(
                            userLocation, 19.0f
                        ))
                    }
                }
            setOnMapClickListener { setBottomSheetVisibility(false) }
            setOnCameraMoveListener {
                setBottomSheetVisibility(false)
                if(this@MapsActivity::marker.isInitialized)
                    marker.position = googleMap.cameraPosition.target;//to center in map
            }
        }
    }

    private fun onMarkerClicked(marker: Marker) {
        bottomSheetView.city_name.text = marker.title

        setBottomSheetVisibility(true)
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val updatedState: Int
        if (isVisible) {
            updatedState = BottomSheetBehavior.STATE_EXPANDED
            next_button.visibility = View.GONE
            my_position.visibility = View.GONE
            map_style.visibility = View.GONE
            bottomSheetBehavior.state = updatedState
            guide_text.setText(R.string.select_delivery_time_and_payment_method)
        } else {
            updatedState = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = updatedState
            next_button.visibility = View.VISIBLE
            my_position.visibility = View.VISIBLE
            map_style.visibility = View.VISIBLE
            guide_text.setText(R.string.move_map_to_select_vehicle_location)
        }

    }


    override fun onStart() {
        super.onStart();

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            enableLocationSettings()
        }
    }


    private fun enableLocationSettings() {
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}
