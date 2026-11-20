/** Modela los datos JSON recibidos desde el servicio remoto. */
package com.example.nasaexplorer.data.remote.donki

import com.google.gson.annotations.SerializedName

/** Representa la estructura de datos intercambiada con una API. */
data class DonkiNotificationDto(
    @SerializedName("messageType")
    val messageType: String = "",
    @SerializedName("messageIssueTime")
    val messageIssueTime: String = "",
    @SerializedName("messageBody")
    val messageBody: String = "",
    @SerializedName("messageURL")
    val messageUrl: String? = null
)
