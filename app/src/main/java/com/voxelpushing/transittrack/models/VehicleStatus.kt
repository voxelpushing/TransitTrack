package com.voxelpushing.transittrack.models

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.voxelpushing.transittrack.R

@Entity(tableName = "vehicle_status")
data class VehicleStatus(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "vehicle_id") val id: Int,
    @ColumnInfo(name = "route_tag") val routeTag: Int,
    @ColumnInfo(name = "dir_tag") val dirTag: String?,
    @ColumnInfo(name = "dir_desc")val dirDescription: String?,
    @ColumnInfo(name = "vehicle_lat") val lat: Double,
    @ColumnInfo(name = "vehicle_lon") val lon: Double,
    val time: Long
) {
    constructor(vlr: VehicleLocationResponse.Result, routeDesc: String?, time: Long) : this(
        0,
        vlr.vehicle.id,
        vlr.vehicle.routeTag,
        vlr.vehicle.dirTag,
        routeDesc,
        vlr.vehicle.lat,
        vlr.vehicle.lon,
        time
    )

    fun getRouteNumber(context: Context?): String {
        return if (routeTag == 0 && context != null) {
            context.getString(R.string.NIS)
        } else {
            routeTag.toString()
        }
    }

    fun isInService(): Boolean {
        return routeTag == 0
    }

}