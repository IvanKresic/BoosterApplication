package com.ivankresicl.boosterapp.models

import com.google.gson.annotations.SerializedName

/**
 * Created by ivan on 07/07/2022
 */
data class OrderModel(
    @SerializedName("deliveryTime") var deliveryTime: String,
    @SerializedName("paymentType") var paymentType: String,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("latitude") var latitude: Double
)
