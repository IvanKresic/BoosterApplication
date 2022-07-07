package com.ivankresicl.boosterapp.api

import com.ivankresicl.boosterapp.models.CancelModel
import com.ivankresicl.boosterapp.models.OrderModel
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by ivan on 07/07/2022
 */
interface Api {
    companion object {//192.168.2.229
        const val BASE_URL = "http://192.168.2.229:8080/"
    }


    @POST("requestboostorder")
    fun requestBoostOrder(@Body orderModel: OrderModel): Call<OrderModel?>?

    @GET("cancelboostorder")
    fun cancelBoostOrder(): Call<CancelModel?>?
}