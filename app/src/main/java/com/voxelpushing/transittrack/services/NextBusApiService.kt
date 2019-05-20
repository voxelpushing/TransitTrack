package com.voxelpushing.transittrack.services

import com.voxelpushing.transittrack.models.RouteConfigResponse
import com.voxelpushing.transittrack.models.VehicleLocationResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NextBusApiService {

    companion object {
        fun create(): NextBusApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://webservices.nextbus.com/service/")
                .build()

            return retrofit.create(NextBusApiService::class.java)
        }
    }

    // Queries for the vehicle location using a vehicle number
    @GET("publicJSONFeed?command=vehicleLocation")
    fun getVehicleLocation(@Query("a") agencyTag: String,
                           @Query("v") vehicleId: String): Observable<VehicleLocationResponse.Result>

    // Queries for the description of the dirTag returned from getVehicleLocation
    @GET("publicJSONFeed?command=routeConfig")
    fun getRouteConfig(@Query("a") agencyTag: String,
                       @Query("r") routeTag: String): Observable<RouteConfigResponse.Result>
}