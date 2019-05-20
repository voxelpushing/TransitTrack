package com.voxelpushing.transittrack.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.voxelpushing.transittrack.models.VehicleStatus

@Database(entities = [VehicleStatus::class], version = 1)
abstract class VehicleStatusDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: VehicleStatusDatabase? = null

        fun getVehicleStatusDatabase(context: Context): VehicleStatusDatabase {

            INSTANCE?.let {
                return it
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VehicleStatusDatabase::class.java,
                    "vehicle_status_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    abstract fun vehicleStatusDao(): VehicleStatusDao
}