package com.voxelpushing.transittrack.models

object VehicleLocationResponse {
    data class Result(val vehicle: Vehicle)
    data class Vehicle(
        val id: Int,
        val routeTag: Int,
        val dirTag: String?,
        val lat: Double,
        val lon: Double
    )
}