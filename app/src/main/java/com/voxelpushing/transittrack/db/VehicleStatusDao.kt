package com.voxelpushing.transittrack.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voxelpushing.transittrack.models.VehicleStatus
import io.reactivex.Flowable

@Dao
interface VehicleStatusDao {

    @Query("SELECT * from vehicle_status ORDER BY time DESC")
    fun getAllStatuses(): Flowable<List<VehicleStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVehicleStatus(vehicleStatus: VehicleStatus)
}