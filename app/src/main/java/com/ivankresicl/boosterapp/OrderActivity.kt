package com.ivankresicl.boosterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ivankresicl.boosterapp.api.RetrofitClient
import com.ivankresicl.boosterapp.models.CancelModel
import com.ivankresicl.boosterapp.models.OrderModel
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        delivery_time_value.text = intent.getStringExtra("DELIVERY_TIME")
        payment_value.text = intent.getStringExtra("PAYMENT_TYPE")
        vehicle_location_latitude.text ="LAT: \n" + intent.getDoubleExtra("DELIVERY_LATITUDE", 0.0)
        vehicle_location_longitude.text ="LONG: \n" + intent.getDoubleExtra("DELIVERY_LONGITUDE", 0.0)
        cancel_button.setOnClickListener {

            val requestBoostOrder = RetrofitClient.instance?.getMyApi()?.cancelBoostOrder()
            requestBoostOrder?.enqueue(
                object : Callback<CancelModel?> {
                    override fun onResponse(
                        call: Call<CancelModel?>?,
                        response: Response<CancelModel?>
                    ) {
                        val cancelModel: CancelModel? = response.body()
                        Toast.makeText(
                            applicationContext,
                            cancelModel?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        onBackPressed()
                    }

                    override fun onFailure(call: Call<CancelModel?>?, t: Throwable?) {
                        Toast.makeText(
                            applicationContext,
                            "An error has occured: " + t?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}